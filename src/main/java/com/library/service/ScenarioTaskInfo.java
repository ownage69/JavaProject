package com.library.service;

import com.library.dto.ScenarioTaskState;
import lombok.Getter;

@Getter
final class ScenarioTaskInfo {

    private final ScenarioTaskState status;
    private final String result;
    private final String errorMessage;

    private ScenarioTaskInfo(
            ScenarioTaskState status,
            String result,
            String errorMessage
    ) {
        this.status = status;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    static ScenarioTaskInfo pending() {
        return new ScenarioTaskInfo(ScenarioTaskState.PENDING, null, null);
    }

    ScenarioTaskInfo running() {
        return new ScenarioTaskInfo(ScenarioTaskState.RUNNING, null, null);
    }

    ScenarioTaskInfo completed(String taskResult) {
        return new ScenarioTaskInfo(ScenarioTaskState.COMPLETED, taskResult, null);
    }

    ScenarioTaskInfo failed(String taskErrorMessage) {
        return new ScenarioTaskInfo(ScenarioTaskState.FAILED, null, taskErrorMessage);
    }

    boolean isTerminal() {
        return status == ScenarioTaskState.COMPLETED
                || status == ScenarioTaskState.FAILED;
    }
}
