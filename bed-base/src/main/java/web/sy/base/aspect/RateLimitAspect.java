package web.sy.base.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import web.sy.base.annotation.AnonymousRateLimit;
import web.sy.base.annotation.RateLimit;
import web.sy.base.security.RateLimitManager;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitManager rateLimitManager;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        log.debug("进入限流切面处理 - 方法: {}.{}",
            point.getSignature().getDeclaringTypeName(),
            point.getSignature().getName());
            
        String key = generateKey(point, rateLimit, rateLimit.limitByUser());
        log.debug("Rate limit key: {}, limit: {}, time: {} {}", 
            key, rateLimit.count(), rateLimit.time(), rateLimit.timeUnit());
            
        rateLimitManager.checkRateLimit(key, rateLimit.count(), rateLimit.time(), rateLimit.timeUnit());
        return point.proceed();
    }

    @Around("@within(web.sy.base.annotation.RateLimit)")
    public Object rateLimitClass(ProceedingJoinPoint point) throws Throwable {
        log.debug("进入类级别限流切面处理");
        RateLimit rateLimit = point.getTarget().getClass().getAnnotation(RateLimit.class);
        if (rateLimit != null) {
            RateLimit methodRateLimit = getMethodRateLimit(point);
            if (methodRateLimit != null) {
                return rateLimit(point, methodRateLimit);
            }
            return rateLimit(point, rateLimit);
        }
        return point.proceed();
    }

    @Around("@annotation(anonymousRateLimit)")
    public Object anonymousRateLimit(ProceedingJoinPoint point, AnonymousRateLimit anonymousRateLimit) throws Throwable {
        String key = generateAnonymousKey(point, anonymousRateLimit);
        rateLimitManager.checkRateLimit(key, anonymousRateLimit.count(), 
            anonymousRateLimit.time(), anonymousRateLimit.timeUnit());
        
        return point.proceed();
    }

    private AnonymousRateLimit getMethodAnonymousRateLimit(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        return signature.getMethod().getAnnotation(AnonymousRateLimit.class);
    }

    private String generateAnonymousKey(ProceedingJoinPoint point, AnonymousRateLimit rateLimit) {
        StringBuilder key = new StringBuilder(rateLimit.prefix());
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        key.append(signature.getDeclaringTypeName());
        
        if (getMethodAnonymousRateLimit(point) != null) {
            key.append(".").append(signature.getName());
        }

        key.append(":").append(getClientIp());

        log.debug("Generated anonymous rate limit key: {}", key);
        return key.toString();
    }

    private RateLimit getMethodRateLimit(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        return signature.getMethod().getAnnotation(RateLimit.class);
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        return attributes.getRequest().getRemoteAddr();
    }

    private String generateKey(ProceedingJoinPoint point, RateLimit rateLimit, boolean limitByUser) {
        StringBuilder key = new StringBuilder(rateLimit.prefix());
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        key.append(signature.getDeclaringTypeName());
        
        if (getMethodRateLimit(point) != null) {
            key.append(".").append(signature.getName());
        }

        if (limitByUser) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "anonymous";
            key.append(":").append(username);
        } else {
            key.append(":").append(getClientIp());
        }

        log.debug("Generated rate limit key: {}", key);
        return key.toString();
    }
} 