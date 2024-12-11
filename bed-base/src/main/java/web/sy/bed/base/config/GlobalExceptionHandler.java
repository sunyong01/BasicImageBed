package web.sy.bed.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import web.sy.bed.base.exception.*;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.utils.ResponseBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理系统初始化相关异常
     */
    @ExceptionHandler({
        SystemNotInitializedException.class,
        NoSuchBeanDefinitionException.class,
        BeanCreationException.class
    })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseInfo<?> handleInitializationException(Exception ex) {
        log.warn("System initialization error: {}", ex.getMessage());
        return ResponseBuilder.error("System not initialized. Please complete initialization first.");
    }

    /**
     * 处理认证相关异常
     */
    @ExceptionHandler({
        AuthenticationException.class,
        TokenAuthException.class,
        io.jsonwebtoken.ExpiredJwtException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseInfo<?> handleAuthenticationException(Exception ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseBuilder.error(ex.getMessage());
    }

    /**
     * 处理访问权限相关异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseInfo<?> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseBuilder.error("Access denied: insufficient permissions");
    }

    /**
     * 处理请求参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseBuilder.error(errors, "参数验证失败!");
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo<?> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseBuilder.error(message);
    }

    /**
     * 处理限流异常
     */
    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseInfo<?> handleRateLimitException(RateLimitException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseBuilder.error(ex.getMessage());
    }

    /**
     * 处理业务逻辑异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo<?> handleBusinessException(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseBuilder.error(ex.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseInfo<?> handleException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseBuilder.error("Internal Server Error: " + ex.getMessage());
    }
}
