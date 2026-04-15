package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.RaceConditionDemoResultDto;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService raceConditionDemoService =
            new RaceConditionDemoService();

    @AfterEach
    void clearInterruptedFlag() {
        Thread.interrupted();
    }

    @Test
    void runDemoShouldShowLostUpdatesForUnsafeCounterAndCorrectSafeCounters() {
        RaceConditionDemoResultDto result = raceConditionDemoService.runDemo(64, 1000);

        assertThat(result.getThreadCount()).isEqualTo(64);
        assertThat(result.getIncrementsPerThread()).isEqualTo(1000);
        assertThat(result.getExpectedValue()).isEqualTo(64000);
        assertThat(result.getUnsafeCounterValue()).isLessThan(result.getExpectedValue());
        assertThat(result.getSynchronizedCounterValue()).isEqualTo(result.getExpectedValue());
        assertThat(result.getAtomicCounterValue()).isEqualTo(result.getExpectedValue());
        assertThat(result.isRaceConditionObserved()).isTrue();
        assertThat(result.getLostUpdates())
                .isEqualTo(result.getExpectedValue() - result.getUnsafeCounterValue());
    }

    @Test
    void runDemoShouldNotReportRaceConditionForSingleThread() {
        RaceConditionDemoResultDto result = raceConditionDemoService.runDemo(1, 1);

        assertThat(result.getExpectedValue()).isEqualTo(1);
        assertThat(result.getUnsafeCounterValue()).isEqualTo(1);
        assertThat(result.getSynchronizedCounterValue()).isEqualTo(1);
        assertThat(result.getAtomicCounterValue()).isEqualTo(1);
        assertThat(result.isRaceConditionObserved()).isFalse();
        assertThat(result.getLostUpdates()).isZero();
    }

    @Test
    void awaitStartShouldThrowWhenInterrupted() {
        CountDownLatch startLatch = new CountDownLatch(1);
        Thread.currentThread().interrupt();

        assertThatThrownBy(() -> invokePrivate("awaitStart", CountDownLatch.class, startLatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Race condition demo was interrupted")
                .hasCauseInstanceOf(InterruptedException.class);
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void waitForCompletionShouldThrowWhenInterrupted() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = mock(Future.class);
        List<Future<Object>> futures = List.of(future);
        when(future.get()).thenThrow(new InterruptedException("stop"));

        assertThatThrownBy(() -> invokePrivate("waitForCompletion", List.class, futures))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Race condition demo was interrupted")
                .hasCauseInstanceOf(InterruptedException.class);
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void waitForCompletionShouldWrapExecutionExceptionCause() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = mock(Future.class);
        List<Future<Object>> futures = List.of(future);
        when(future.get()).thenThrow(new ExecutionException(new IllegalArgumentException("boom")));

        assertThatThrownBy(() -> invokePrivate("waitForCompletion", List.class, futures))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Race condition demo failed")
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shutdownExecutorShouldThrowWhenTerminationTimesOut() throws Exception {
        ExecutorService executorService = mock(ExecutorService.class);
        when(executorService.awaitTermination(5L, TimeUnit.SECONDS)).thenReturn(false);
        when(executorService.shutdownNow()).thenReturn(List.of(() -> {
        }));

        assertThatThrownBy(() -> invokePrivate(
                "shutdownExecutor",
                ExecutorService.class,
                executorService
        )).isInstanceOf(IllegalStateException.class)
                .hasMessage("Race condition demo did not finish in time. Cancelled tasks: 1");
        verify(executorService).shutdown();
        verify(executorService).shutdownNow();
    }

    @Test
    void shutdownExecutorShouldThrowWhenShutdownIsInterrupted() throws Exception {
        ExecutorService executorService = mock(ExecutorService.class);
        when(executorService.awaitTermination(5L, TimeUnit.SECONDS))
                .thenThrow(new InterruptedException("stop"));
        when(executorService.shutdownNow()).thenReturn(List.of());

        assertThatThrownBy(() -> invokePrivate(
                "shutdownExecutor",
                ExecutorService.class,
                executorService
        )).isInstanceOf(IllegalStateException.class)
                .hasMessage("Race condition demo shutdown was interrupted")
                .hasCauseInstanceOf(InterruptedException.class);
        verify(executorService).shutdown();
        verify(executorService).shutdownNow();
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    private Object invokePrivate(
            String methodName,
            Class<?> parameterType,
            Object argument
    ) throws Exception {
        Method method = RaceConditionDemoService.class.getDeclaredMethod(methodName, parameterType);
        method.setAccessible(true);
        try {
            return method.invoke(raceConditionDemoService, argument);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof Exception checkedException) {
                throw checkedException;
            }
            throw exception;
        }
    }
}
