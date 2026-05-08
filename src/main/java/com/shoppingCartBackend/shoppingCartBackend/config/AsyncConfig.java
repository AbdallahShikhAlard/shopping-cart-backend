package com.shoppingCartBackend.shoppingCartBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5); // عدد Threads الأساسي
        executor.setMaxPoolSize(10); // الحد الأقصى
        executor.setQueueCapacity(50); // عدد الطلبات المنتظرة

        executor.setThreadNamePrefix("Ecommerce-Thread-");
        executor.initialize();

        return executor;
    }
}