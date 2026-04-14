package com.library.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

class AsyncConfigTest {

    @Test
    void scenarioTaskExecutorShouldUseConfiguredPoolSettings() {
        AsyncConfig asyncConfig = new AsyncConfig();
        ReflectionTestUtils.setField(asyncConfig, "corePoolSize", 6);
        ReflectionTestUtils.setField(asyncConfig, "maxPoolSize", 12);
        ReflectionTestUtils.setField(asyncConfig, "queueCapacity", 128);
        ReflectionTestUtils.setField(asyncConfig, "awaitTerminationSeconds", 45);

        ThreadPoolTaskExecutor executor =
                (ThreadPoolTaskExecutor) asyncConfig.scenarioTaskExecutor();

        assertThat(executor.getCorePoolSize()).isEqualTo(6);
        assertThat(executor.getMaxPoolSize()).isEqualTo(12);
        assertThat(ReflectionTestUtils.getField(executor, "queueCapacity")).isEqualTo(128);
        assertThat(ReflectionTestUtils.getField(executor, "threadNamePrefix"))
                .isEqualTo("scenario-task-");
        assertThat(ReflectionTestUtils.getField(
                executor,
                "waitForTasksToCompleteOnShutdown"
        )).isEqualTo(true);
        assertThat(ReflectionTestUtils.getField(executor, "awaitTerminationMillis"))
                .isEqualTo(45000L);

        executor.shutdown();
    }
}
