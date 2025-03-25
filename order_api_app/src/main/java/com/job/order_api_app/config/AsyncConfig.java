package com.job.order_api_app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final AsyncConfigProperties asyncConfigProperties;

    @Bean(name = "asyncTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncConfigProperties.getCoreSize());
        executor.setMaxPoolSize(asyncConfigProperties.getMaxSize());
        executor.setQueueCapacity(asyncConfigProperties.getQueueCapacity());
        executor.setThreadNamePrefix(asyncConfigProperties.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(asyncConfigProperties.isWaitForTasks());
        executor.initialize();
        return executor;
    }
}
