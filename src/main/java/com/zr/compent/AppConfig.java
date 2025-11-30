package com.zr.compent;

import com.zr.constant.Constants;
import com.zr.constant.RedissonConfig;
import com.zr.pojo.PlateInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

@Configuration
@Import(MyImportSelector.class) // ← 关键：导入普通 ImportSelector
public class AppConfig {

    @Bean
    @Scope(proxyMode = ScopedProxyMode.NO)
    public ThreadPoolExecutor executorService() {

        ThreadFactory springThreadFactory = new CustomizableThreadFactory("springThread-pool-");

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(Constants.CPU_COUNT * 2,
                Constants.CPU_COUNT * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024),
                springThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());

        return executorService;
    }

    @Bean
    @ConditionalOnBean(PlateInfo.class)
    public SpringFactoriesTestBean springFactoriesTestBean() {
        System.out.println("===========AppConfig init SpringFactoriesTestBean");
        return new SpringFactoriesTestBean();
    }

    @Bean("redissonConfig")
    public RedissonConfig redissonConfig() {
        System.out.println("===========AppConfig init SpringFactoriesTestBean");
        return new RedissonConfig();
    }

}