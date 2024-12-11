package web.sy.bed.base.security;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import web.sy.bed.base.annotation.ApiTokenSupport;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.config.condition.InitializedCondition;
import web.sy.bed.base.exception.TokenAuthException;
import web.sy.bed.base.pojo.entity.User;
import web.sy.bed.base.pojo.entity.UserToken;
import web.sy.bed.base.service.UserService;
import web.sy.bed.base.service.UserTokenService;
import web.sy.bed.base.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
;

@Lazy
@Component
@Slf4j
@Conditional(InitializedCondition.class)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectProvider<UserService> userServiceProvider;

    private final Set<String> anonymousUrls;
    private final Set<String> apiTokenUrls;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final ObjectProvider<UserTokenService> userTokenServiceProvider;

    @Resource
    private JwtTokenCacheManager jwtTokenCacheManager;

    public JwtAuthenticationFilter(
            ObjectProvider<UserService> userServiceProvider, 
            @Qualifier("anonymousUrls") Set<String> anonymousUrls,
            @Qualifier("apiTokenUrls") Set<String> apiTokenUrls,
            ObjectProvider<UserTokenService> userTokenServiceProvider) {
        this.userServiceProvider = userServiceProvider;
        this.anonymousUrls = anonymousUrls;
        this.apiTokenUrls = apiTokenUrls;
        this.userTokenServiceProvider = userTokenServiceProvider;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String swaggerUrl : GlobalConfig.getSwaggerUrls()) {
            if (pathMatcher.match(swaggerUrl, path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        // Check if the request URI is in the anonymous URLs set
        if (isAnonymousUrl(requestURI)) {
            log.debug("Skipping JWT authentication for anonymous URL: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = request.getHeader("Authorization");
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            log.debug("Received JWT token: {}", token);
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                log.debug("substr token: {}", token);
                // 检查是否是API Token格式 (id|token) 且URL支持API Token
                if (isApiTokenUrl(requestURI) && token.contains("|")) {
                    log.debug("Received API Token: {}", token);
                    handleApiToken(token, request);
                } else {
                    // 检查token是否在白名单中
                    if (!jwtTokenCacheManager.isTokenValid(token)) {
                        throw new TokenAuthException("Invalid or expired token");
                    }
                    handleJwtToken(token, request);
                }
            }
        } catch (TokenAuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleApiToken(String token, HttpServletRequest request) {
        String[] parts = token.split("\\|");
        if (parts.length != 2) {
            throw new TokenAuthException("Invalid API token format");
        }

        Long tokenId;
        try {
            tokenId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new TokenAuthException("Invalid token ID");
        }

        // 验证API Token - 使用 ObjectProvider
        UserToken userToken = Objects.requireNonNull(userTokenServiceProvider.getIfAvailable())
            .validateToken(tokenId, parts[1]);
        if (userToken == null || !userToken.getEnabled()) {
            throw new TokenAuthException("Invalid or disabled API token");
        }

        // 获取用户信息
        User user = Objects.requireNonNull(userServiceProvider.getIfAvailable())
            .findById(userToken.getUserId());
        if (user == null) {
            throw new TokenAuthException("User not found");
        }

        // 创建认证信息
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleJwtToken(String token, HttpServletRequest request) {
        String username = JwtTokenUtil.getUsernameFromToken(token);
        String tokenType = JwtTokenUtil.getTokenType(token);
        
        if (!"access".equals(tokenType)) {
            throw new TokenAuthException("Invalid token type");
        }

        User user = getUserDetails(username);
        if (user == null || !JwtTokenUtil.validateToken(token, user)) {
            throw new TokenAuthException("Invalid token");
        }

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());  // 将token存储在credentials中
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 根据用户名获取用户信息的方法
    private User getUserDetails(String username) {
        return Objects.requireNonNull(userServiceProvider.getIfAvailable()).findByUsername(username);
    }

    private boolean isAnonymousUrl(String requestURI) {
        return anonymousUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private boolean isApiTokenUrl(String requestURI) {
        return apiTokenUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

}
