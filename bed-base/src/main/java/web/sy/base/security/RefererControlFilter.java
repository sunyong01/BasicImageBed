package web.sy.base.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import web.sy.base.config.GlobalConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

@Slf4j
public class RefererControlFilter extends OncePerRequestFilter {

    private static final String[] PROTECTED_PATHS = {"/thumb", "/image"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        boolean isProtectedPath = Arrays.stream(PROTECTED_PATHS)
                .anyMatch(requestUri::startsWith);

        if (isProtectedPath && GlobalConfig.getConfig().getEnableRefererControl()) {
            String referer = request.getHeader("Referer");
            log.debug("Checking referer control for path: {}, referer: {}", requestUri, referer);

            if (referer == null || referer.isEmpty()) {
                if (!GlobalConfig.getConfig().getAllowEmptyReferer()) {
                    log.debug("Empty referer is not allowed");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return;
                }
            } else {
                try {
                    URI refererUri = new URI(referer);
                    String refererHost = refererUri.getHost();
                    boolean isAllowedReferer = Arrays.stream(GlobalConfig.getConfig().getAllowedReferers())
                            .anyMatch(allowed -> matchesPattern(refererHost, allowed));
                    
                    if (!isAllowedReferer) {
                        log.debug("Referer {} is not in allowed list", referer);
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        return;
                    }
                } catch (Exception e) {
                    log.debug("Invalid referer format: {}", referer);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return;
                }
            }
            log.debug("Referer check passed");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 检查主机名是否匹配模式
     * 支持的通配符格式:
     * - *.example.com
     * - example.com
     * - sub.example.com
     */
    private boolean matchesPattern(String host, String pattern) {
        if (pattern.startsWith("*.")) {
            // 处理 *.example.com 格式
            String domain = pattern.substring(2);
            return host.endsWith(domain) && host.length() > domain.length();
        } else {
            // 精确匹配
            return host.equals(pattern);
        }
    }
} 