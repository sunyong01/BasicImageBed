package web.sy.bed.base.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 限流时间窗口，默认1秒
     */
    int time() default 1;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限制次数，默认每秒10次
     */
    int count() default 10;

    /**
     * 限流key的前缀
     */
    String prefix() default "";

    /**
     * 是否按用户限流，默认false 
     */
    boolean limitByUser() default false;
} 