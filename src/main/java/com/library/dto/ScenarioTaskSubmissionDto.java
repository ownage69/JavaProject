package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after asynchronous task submission")
public class ScenarioTaskSubmissionDto {

    @Schema(description = "Generated asynchronous task identifier", example = "1")
    private Long taskId;

    @Schema(description = "Current task status", example = "PENDING")
    private ScenarioTaskState status;
}
