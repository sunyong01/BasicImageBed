package web.sy.bed.base.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnonymousRateLimit {
    /**
     * 限流时间窗口，默认1秒
     */
    int time() default 1;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限制次数，默认每秒50次
     */
    int count() default 50;

    /**
     * 限流key的前缀
     */
    String prefix() default "";
} 