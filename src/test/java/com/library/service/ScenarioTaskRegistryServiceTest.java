package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.library.dto.ScenarioTaskState;
import com.library.dto.ScenarioTaskStatusDto;
import org.junit.jupiter.api.Test;

class ScenarioTaskRegistryServiceTest {

    private final ScenarioTaskRegistryService scenarioTaskRegistryService =
            new ScenarioTaskRegistryService();

    @Test
    void registerTaskShouldCreatePendingTaskAndIncrementSubmittedCounter() {
        Long taskId = scenarioTaskRegistryService.registerTask();

        ScenarioTaskStatusDto taskStatus = scenarioTaskRegistryService.getTaskStatus(taskId);

        assertThat(taskId).isEqualTo(1L);
        assertThat(taskStatus.getStatus()).isEqualTo(ScenarioTaskState.PENDING);
        assertThat(taskStatus.getSubmittedTasks()).isEqualTo(1L);
        assertThat(taskStatus.getRunningTasks()).isZero();
        assertThat(taskStatus.getCompletedTasks()).isZero();
        assertThat(taskStatus.getFailedTasks()).isZero();
    }

    @Test
    void markCompletedShouldStoreResultAndUpdateCounters() {
        Long taskId = scenarioTaskRegistryService.registerTask();

        scenarioTaskRegistryService.markRunning(taskId);
        scenarioTaskRegistryService.markCompleted(taskId, "Scenario with transaction completed");

        ScenarioTaskStatusDto taskStatus = scenarioTaskRegistryService.getTaskStatus(taskId);

        assertThat(taskStatus.getStatus()).isEqualTo(ScenarioTaskState.COMPLETED);
        assertThat(taskStatus.getResult()).isEqualTo("Scenario with transaction completed");
        assertThat(taskStatus.getRunningTasks()).isZero();
        assertThat(taskStatus.getCompletedTasks()).isEqualTo(1);
        assertThat(taskStatus.getFailedTasks()).isZero();
    }

    @Test
    void markFailedShouldStoreErrorAndUpdateCounters() {
        Long taskId = scenarioTaskRegistryService.registerTask();
        IllegalStateException exception = new IllegalStateException("Task failed");

        scenarioTaskRegistryService.markRunning(taskId);
        scenarioTaskRegistryService.markFailed(taskId, exception);

        ScenarioTaskStatusDto taskStatus = scenarioTaskRegistryService.getTaskStatus(taskId);

        assertThat(taskStatus.getStatus()).isEqualTo(ScenarioTaskState.FAILED);
        assertThat(taskStatus.getErrorMessage()).isEqualTo("Task failed");
        assertThat(taskStatus.getRunningTasks()).isZero();
        assertThat(taskStatus.getCompletedTasks()).isZero();
        assertThat(taskStatus.getFailedTasks()).isEqualTo(1);
    }

    @Test
    void getTaskStatusShouldThrowWhenTaskDoesNotExist() {
        assertThatThrownBy(() -> scenarioTaskRegistryService.getTaskStatus(99L))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Task not found with id: 99");
    }
}
