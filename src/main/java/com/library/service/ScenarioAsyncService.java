package com.library.service;

import com.library.dto.ScenarioCreateDto;
import com.library.dto.ScenarioTaskStatusDto;
import com.library.dto.ScenarioTaskSubmissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioAsyncService {

    private final ScenarioTaskRegistryService scenarioTaskRegistryService;
    private final ScenarioAsyncExecutor scenarioAsyncExecutor;

    public ScenarioTaskSubmissionDto createWithTransactionAsync(
            ScenarioCreateDto scenarioCreateDto
    ) {
        Long taskId = scenarioTaskRegistryService.registerTask();
        scenarioAsyncExecutor.createWithTransactionAsync(taskId, scenarioCreateDto);
        ScenarioTaskStatusDto taskStatus = scenarioTaskRegistryService.getTaskStatus(taskId);
        return new ScenarioTaskSubmissionDto(taskId, taskStatus.getStatus());
    }

    public ScenarioTaskStatusDto getTaskStatus(Long taskId) {
        return scenarioTaskRegistryService.getTaskStatus(taskId);
    }
}
