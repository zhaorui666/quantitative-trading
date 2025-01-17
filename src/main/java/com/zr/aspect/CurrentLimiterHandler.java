package com.zr.aspect;

import com.zr.annotation.CurrentLimiter;
import com.zr.constant.LimitType;
import com.zr.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class CurrentLimiterHandler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<Long> getRedisScript;

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("currentLimiter.lua")));
        log.info("[分布式限流处理器]脚本加载完成");
    }

    @Around("@annotation(currentLimiter)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, CurrentLimiter currentLimiter) throws Throwable {

        log.debug("[分布式限流处理器]开始执行限流操作");

        // 限流模块key
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();

        StringBuilder limitKey = new StringBuilder(currentLimiter.key());
        if (currentLimiter.limitType() == LimitType.IP) {
            limitKey.append(IpUtil.getIpAddress());
        }

        // 目标类、方法
        String className = signature.getMethod().getDeclaringClass().getName();
        String name = signature.getMethod().getName();
        limitKey.append("_").append(className).append("#").append(name);

        // 限流阈值
        long limitCount = currentLimiter.limit();
        // 限流超时时间
        long expireTime = currentLimiter.expire();
        log.info("[分布式限流处理器]参数值为：method={},limitKey={},limitCount={},limitTimeout={}", name, limitKey, limitCount, expireTime);

        List<String> keyList = new ArrayList<>();
        keyList.add(limitKey.toString());

        List<String> valueList = new ArrayList<>();
        valueList.add(String.valueOf(expireTime));
        valueList.add(String.valueOf(System.currentTimeMillis()));
        valueList.add(String.valueOf(limitCount));

        //key、value序列化器必须设置,否则类型转换为nil
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // 执行Lua脚本
        Long result = redisTemplate.execute(getRedisScript, keyList, valueList.toArray());
        log.info("[分布式限流处理器]限流执行结果-result={}", result);

        if (result >= limitCount) {
            log.warn("由于超过单位时间={}；允许的请求次数={}[触发限流]", expireTime, limitCount);
            String message = currentLimiter.message();
            throw new Exception(message);
        }

        return proceedingJoinPoint.proceed();
    }
}
