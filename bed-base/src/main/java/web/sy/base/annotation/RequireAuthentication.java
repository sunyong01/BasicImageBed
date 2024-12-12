package web.sy.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthentication {
    // 是否需要管理员权限
    boolean requireAdmin() default false;
    
    // 是否需要超级管理员权限
    boolean requireSuperAdmin() default false;
} 