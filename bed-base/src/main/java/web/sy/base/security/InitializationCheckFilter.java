package web.sy.base.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import web.sy.base.config.condition.NotInitializedCondition;
import web.sy.base.utils.ResponseBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
@Conditional(NotInitializedCondition.class)
public class InitializationCheckFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] ALLOWED_PATHS = {
            "/",
            "/api/v1/init",
            "/api/v1/check",
            "/error",
            "/swagger-ui",
            "/v3/api-docs",
            "/static"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;


        String path = httpRequest.getRequestURI();

        // 检查是否是允许的路径
        if (isAllowedPath(path)) {
            chain.doFilter(request, response);
        } else {
            log.warn("System not initialized, redirecting request: {}", path);
            httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String responseBody = objectMapper.writeValueAsString(
                    ResponseBuilder.error("NOT_INITIALIZED", "系统未初始化")
            );
            httpResponse.getWriter().write(responseBody);
        }
    }

    private boolean isAllowedPath(String path) {
        if (path == null) return false;
        log.debug("Checking path: {}", path);
        return Arrays.stream(ALLOWED_PATHS).anyMatch(path::startsWith);
    }
}