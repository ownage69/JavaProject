package com.library.service;

import com.library.dto.ScenarioCreateDto;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioAsyncExecutor {

    private final ScenarioService scenarioService;
    private final ScenarioTaskRegistryService scenarioTaskRegistryService;
    private final ScenarioAsyncDelayService scenarioAsyncDelayService;

    @Async("scenarioTaskExecutor")
    public CompletableFuture<String> createWithTransactionAsync(
            Long taskId,
            ScenarioCreateDto scenarioCreateDto
    ) {
        scenarioTaskRegistryService.markRunning(taskId);
        try {
            scenarioAsyncDelayService.simulateProcessingDelay();
            String result = scenarioService.createWithTransaction(scenarioCreateDto);
            scenarioTaskRegistryService.markCompleted(taskId, result);
            log.debug("Async scenario task {} completed successfully", taskId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception exception) {
            scenarioTaskRegistryService.markFailed(taskId, exception);
            log.error(
                    "Async scenario task {} failed: {}",
                    taskId,
                    exception.getMessage(),
                    exception
            );
            return CompletableFuture.failedFuture(exception);
        }
    }
}
