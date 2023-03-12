package com.example.compent;

import com.example.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

@Lazy
@Configuration
public class AppConfig {

    @Bean
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
