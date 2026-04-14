package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Status of asynchronous scenario execution")
public class ScenarioTaskStatusDto {

    @Schema(description = "Asynchronous task identifier", example = "1")
    private Long taskId;

    @Schema(description = "Current task status", example = "RUNNING")
    private ScenarioTaskState status;

    @Schema(description = "Business result when the task completes successfully")
    private String result;

    @Schema(description = "Error message when the task fails")
    private String errorMessage;

    @Schema(description = "Number of submitted tasks", example = "3")
    private long submittedTasks;

    @Schema(description = "Number of tasks currently in progress", example = "1")
    private int runningTasks;

    @Schema(description = "Number of successfully completed tasks", example = "2")
    private int completedTasks;

    @Schema(description = "Number of failed tasks", example = "0")
    private int failedTasks;
}
