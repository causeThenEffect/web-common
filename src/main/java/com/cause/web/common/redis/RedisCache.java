package com.cause.web.common.redis;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCache {

    String name() default "";

    int ttl() default 10;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
