package web.sy.basicimagebed.interceptor;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import web.sy.basicimagebed.configuration.ConfigProperties;
import web.sy.basicimagebed.exception.TokenAuthException;
import web.sy.basicimagebed.service.TokenAuthService;



@Component
public class LoginIntercept implements HandlerInterceptor {

    @Resource
    TokenAuthService tokenAuthService;

    @Resource
    ConfigProperties configProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果是注册请求，直接放行
        if("POST".equals(request.getMethod()) && request.getRequestURI().equals("/api/v1/tokens")) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        // 如果是允许游客上传，且是上传请求，直接放行
        if (configProperties.isAllowGuest() && "POST".equals(request.getMethod()) && request.getRequestURI().equals("/api/v1/upload")) {
            request.setAttribute("userEmail", "guest");
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        // 从Header中获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new TokenAuthException("Token 错误");
        }
        String userEmail = tokenAuthService.tokenAuth(token);
        if (userEmail.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new TokenAuthException("Token 不存在");
        }
        request.setAttribute("userEmail", userEmail);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
