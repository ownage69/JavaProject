package com.library.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${library.scenario.async.core-pool-size:16}")
    private int corePoolSize;

    @Value("${library.scenario.async.max-pool-size:64}")
    private int maxPoolSize;

    @Value("${library.scenario.async.queue-capacity:2000}")
    private int queueCapacity;

    @Value("${library.scenario.async.await-termination-seconds:30}")
    private int awaitTerminationSeconds;

    @Bean(name = "scenarioTaskExecutor")
    public Executor scenarioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("scenario-task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}
