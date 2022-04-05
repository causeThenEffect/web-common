package com.cause.web.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RedisCacheAspect<K, V> {

    @Autowired
    RedissonClient redissonClient;

    @Around("@annotation(redisCache) && execution(java.util.Map *(java.util.List))")
    public Object redisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {
        Map<K, V> resultMap = new HashMap<>();

        try {
            // 从缓存获取
            String cacheName = redisCache.name();
            Map<K, V> cacheMap = new HashMap<>();
            List<K> missCacheIds = new ArrayList<>();
            Object[] args = joinPoint.getArgs();
            Collection<K> collection = (Collection<K>) args[0];

            collection.forEach(id -> {
                RBucket<V> rBucket = redissonClient.getBucket(cacheName + ":" + id);
                V value = rBucket.get();
                if (value != null) {
                    cacheMap.put(id, value);
                } else {
                    missCacheIds.add(id);
                }
            });
            resultMap.putAll(cacheMap);

            // 调接口获取数据
            if (missCacheIds.size() > 0) {
                args[0] = missCacheIds;
                Map<K, V> rpcMap = (Map<K, V>) joinPoint.proceed(args);
                int ttl = redisCache.ttl();
                TimeUnit timeUnit = redisCache.timeUnit();
                rpcMap.forEach((key, value) -> {
                    RBucket<V> bucket = redissonClient.getBucket(cacheName + ":" + key);
                    bucket.set(value, ttl, timeUnit);
                });
                resultMap.putAll(rpcMap);
            }
        } catch (Throwable e) {
            log.error("获取redis缓存{}失败，直接从panshi接口获取", redisCache.name(), e);
            resultMap = (Map<K, V>) joinPoint.proceed();
        }
        if (resultMap.size() > 0) {
            log.info("get data from redis");
        } else {
            log.info("no data from redis");
        }
        return resultMap;
    }

}
