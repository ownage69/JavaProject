package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "State of asynchronous scenario execution")
public enum ScenarioTaskState {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}
