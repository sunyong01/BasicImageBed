package web.sy.basicimagebed.configuration;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import web.sy.basicimagebed.interceptor.LoginIntercept;
import web.sy.basicimagebed.interceptor.DebugUrlLoggingHandler;
import web.sy.basicimagebed.interceptor.RegisterIntercept;

@Configuration
public class AppConfiguration implements WebMvcConfigurer {


    @Resource
    LoginIntercept loginIntercept;

    @Resource
    RegisterIntercept registerIntercept;

    @Resource
    DebugUrlLoggingHandler debugUrlLoggingHandler;

    @Resource
    ConfigProperties configProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!configProperties.isAllowGuest()) {
            registry.addInterceptor(loginIntercept)
                    .excludePathPatterns( "/api/v1/register","/user/register")
                    .addPathPatterns("/api/v1/**","/user/**");
        }
        if (!configProperties.isAllowRegister()) {
            registry.addInterceptor(registerIntercept).addPathPatterns("/user/register");
        }
        if (configProperties.isDebug()) {
            registry.addInterceptor(debugUrlLoggingHandler).addPathPatterns("/**");
        }
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
