package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ScenarioAsyncDelayServiceTest {

    private final ScenarioAsyncDelayService scenarioAsyncDelayService =
            new ScenarioAsyncDelayService();

    @AfterEach
    void clearInterruptedFlag() {
        Thread.interrupted();
    }

    @Test
    void simulateProcessingDelayShouldReturnImmediatelyWhenDelayIsZero() {
        ReflectionTestUtils.setField(scenarioAsyncDelayService, "asyncDelayMillis", 0L);

        assertThatCode(scenarioAsyncDelayService::simulateProcessingDelay)
                .doesNotThrowAnyException();
    }

    @Test
    void simulateProcessingDelayShouldSleepWhenDelayIsPositive() {
        ReflectionTestUtils.setField(scenarioAsyncDelayService, "asyncDelayMillis", 1L);

        assertThatCode(scenarioAsyncDelayService::simulateProcessingDelay)
                .doesNotThrowAnyException();
    }

    @Test
    void simulateProcessingDelayShouldThrowWhenThreadIsInterrupted() {
        ReflectionTestUtils.setField(scenarioAsyncDelayService, "asyncDelayMillis", 50L);
        Thread.currentThread().interrupt();

        assertThatThrownBy(scenarioAsyncDelayService::simulateProcessingDelay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Async scenario task was interrupted")
                .hasCauseInstanceOf(InterruptedException.class);
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }
}
