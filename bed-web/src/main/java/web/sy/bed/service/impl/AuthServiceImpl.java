package web.sy.bed.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.entity.User;
import web.sy.base.security.JwtTokenCacheManager;
import web.sy.base.service.UserService;
import web.sy.base.utils.JwtTokenUtil;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.service.AuthService;
import web.sy.bed.vo.req.TokenAuthReqVO;
import web.sy.bed.vo.resp.AuthResponse;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private UserService userService;

    @Resource
    private JwtTokenCacheManager jwtTokenCacheManager;

    @Override
    public ResponseInfo<AuthResponse> login(TokenAuthReqVO loginReq) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginReq.getUsername(),
                    loginReq.getPassword()
                )
            );
            
            User userDetails = (User) authentication.getPrincipal();
            String token = JwtTokenUtil.generateToken(userDetails);
            
            // 将新token加入白名单
            jwtTokenCacheManager.addToken(token, userDetails.getUsername());
            
            // 更新最后登录时间
            userService.updateLastLoginTime(userDetails.getUsername());
            AuthResponse authResponse = new AuthResponse(token, JwtTokenUtil.getExpirationDateFromToken(token), userDetails.getRoles());

            return ResponseBuilder.success(authResponse);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            return ResponseBuilder.error("用户名或密码错误");
        }
    }

    @Override
    public ResponseInfo<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            // 从白名单中移除token
            String token = authentication.getCredentials().toString();
            jwtTokenCacheManager.removeToken(token);
        }
        SecurityContextHolder.clearContext();
        return ResponseBuilder.success("登出成功");
    }

    @Override
    public ResponseInfo<String> refreshToken(String refreshToken) {
        try {
            String username = JwtTokenUtil.getUsernameFromToken(refreshToken);
            User user = userService.findByUsername(username);
            
            if (username != null && JwtTokenUtil.validateRefreshToken(refreshToken, user)) {
                // 获取当前的access token
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                if (currentAuth != null && currentAuth.getCredentials() != null) {
                    // 从白名单中移除旧的access token
                    String oldToken = currentAuth.getCredentials().toString();
                    jwtTokenCacheManager.removeToken(oldToken);
                }

                // 生成新的access token
                String newToken = JwtTokenUtil.generateToken(user);
                // 将新token加入白名单
                jwtTokenCacheManager.addToken(newToken, username);
                
                return ResponseBuilder.success(newToken);
            }
            return ResponseBuilder.error("Invalid refresh token");
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return ResponseBuilder.error("Failed to refresh token");
        }
    }
} 