package com.zr.compent;

import com.zr.constant.Constants;
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
}