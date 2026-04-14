package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.ScenarioCreateDto;
import com.library.dto.ScenarioTaskState;
import com.library.dto.ScenarioTaskStatusDto;
import com.library.dto.ScenarioTaskSubmissionDto;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioAsyncServiceTest {

    @Mock
    private ScenarioTaskRegistryService scenarioTaskRegistryService;

    @Mock
    private ScenarioAsyncExecutor scenarioAsyncExecutor;

    @InjectMocks
    private ScenarioAsyncService scenarioAsyncService;

    @Test
    void createWithTransactionAsyncShouldRegisterTaskAndStartExecution() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        ScenarioTaskStatusDto taskStatus = new ScenarioTaskStatusDto(
                15L,
                ScenarioTaskState.PENDING,
                null,
                null,
                1L,
                0,
                0,
                0
        );
        when(scenarioTaskRegistryService.registerTask()).thenReturn(15L);
        when(scenarioAsyncExecutor.createWithTransactionAsync(15L, scenarioCreateDto))
                .thenReturn(CompletableFuture.completedFuture("done"));
        when(scenarioTaskRegistryService.getTaskStatus(15L)).thenReturn(taskStatus);

        ScenarioTaskSubmissionDto response =
                scenarioAsyncService.createWithTransactionAsync(scenarioCreateDto);

        assertThat(response.getTaskId()).isEqualTo(15L);
        assertThat(response.getStatus()).isEqualTo(ScenarioTaskState.PENDING);
        verify(scenarioAsyncExecutor).createWithTransactionAsync(15L, scenarioCreateDto);
    }

    @Test
    void getTaskStatusShouldDelegateToRegistry() {
        ScenarioTaskStatusDto taskStatus = new ScenarioTaskStatusDto(
                18L,
                ScenarioTaskState.RUNNING,
                null,
                null,
                2L,
                1,
                0,
                0
        );
        when(scenarioTaskRegistryService.getTaskStatus(18L)).thenReturn(taskStatus);

        ScenarioTaskStatusDto response = scenarioAsyncService.getTaskStatus(18L);

        assertThat(response).isEqualTo(taskStatus);
    }

    private ScenarioCreateDto createScenarioCreateDto() {
        return new ScenarioCreateDto(
                "Demo Publisher",
                "Scenario Book",
                "9780306407000",
                1L,
                2L,
                3L,
                LocalDate.now().plusDays(5)
        );
    }
}
