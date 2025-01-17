package com.zr.annotation;
import com.zr.constant.LimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentLimiter {

    /**
     * 限流key
     */
    String key() default "current:limiter:";

    /**
     * 单位时间限制通过请求数
     */
    long limit() default 1;

    /**
     * 过期时间，单位秒
     */
    long expire() default 5;

    /**
     * 限流提示语
     */
    String message() default "访问过于频繁";

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
}