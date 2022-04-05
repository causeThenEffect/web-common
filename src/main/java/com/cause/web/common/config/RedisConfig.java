package com.cause.web.common.config;

import com.cause.web.common.redis.RedisCacheAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheAspect redisCacheAspect() {
        return new RedisCacheAspect();
    }

}
