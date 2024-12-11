package web.sy.bed.base.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import web.sy.bed.base.config.GlobalConfig;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class SwaggerAccessFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug(GlobalConfig.getSwaggerUrls().toString());
        if (GlobalConfig.getConfig().getEnableDefaultSwaggerAccess()) {
            log.debug("Swagger access is enabled.");
        }
        String requestUri = request.getRequestURI();
        boolean isSwaggerUrl = Arrays.stream(GlobalConfig.getSwaggerUrls())
                .anyMatch(url -> {
                    if (url.endsWith("/**")) {
                        String baseUrl = url.substring(0, url.length() - 3);
                        return requestUri.startsWith(baseUrl);
                    }
                    return requestUri.equals(url);
                });

        if (isSwaggerUrl) {
            // 如果是swagger相关的URL，检查是否启用了swagger访问
            if (!GlobalConfig.getConfig().getEnableDefaultSwaggerAccess()) {
                log.debug("Swagger access is disabled. Blocking request to: {}", requestUri);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
            log.debug("Allowing swagger access to: {}", requestUri);
        }

        filterChain.doFilter(request, response);
    }
} 