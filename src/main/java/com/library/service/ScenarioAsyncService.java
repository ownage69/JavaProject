package com.library.service;

import com.library.dto.ScenarioCreateDto;
import com.library.dto.ScenarioTaskStatusDto;
import com.library.dto.ScenarioTaskSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioAsyncService {

    private final ScenarioTaskRegistryService scenarioTaskRegistryService;
    private final ScenarioAsyncExecutor scenarioAsyncExecutor;

    public ScenarioTaskSubmissionDto createWithTransactionAsync(
            ScenarioCreateDto scenarioCreateDto
    ) {
        Long taskId = scenarioTaskRegistryService.registerTask();
        try {
            scenarioAsyncExecutor.createWithTransactionAsync(taskId, scenarioCreateDto);
        } catch (TaskRejectedException exception) {
            scenarioTaskRegistryService.markFailed(taskId, exception);
            log.warn("Async scenario task {} was rejected by executor", taskId, exception);
        }
        ScenarioTaskStatusDto taskStatus = scenarioTaskRegistryService.getTaskStatus(taskId);
        return new ScenarioTaskSubmissionDto(taskId, taskStatus.getStatus());
    }

    public ScenarioTaskStatusDto getTaskStatus(Long taskId) {
        return scenarioTaskRegistryService.getTaskStatus(taskId);
    }
}
