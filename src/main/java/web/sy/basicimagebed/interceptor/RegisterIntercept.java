package web.sy.basicimagebed.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import web.sy.basicimagebed.exception.RegisterPolicyException;

@Component
public class RegisterIntercept  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        throw new RegisterPolicyException("Register is not allowed!");
    }
}
