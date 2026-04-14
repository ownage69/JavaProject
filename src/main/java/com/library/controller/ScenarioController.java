package com.library.controller;

import com.library.dto.RaceConditionDemoResultDto;
import com.library.dto.ScenarioCreateDto;
import com.library.dto.ScenarioTaskStatusDto;
import com.library.dto.ScenarioTaskSubmissionDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.RaceConditionDemoService;
import com.library.service.ScenarioAsyncService;
import com.library.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Scenarios", description = "Transactional scenario demo API")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Resource not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Business or data conflict",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ScenarioAsyncService scenarioAsyncService;
    private final RaceConditionDemoService raceConditionDemoService;

    @PostMapping("/without-transaction")
    @Operation(summary = "Run scenario without transaction")
    public ResponseEntity<String> createWithoutTransaction(
            @Valid @RequestBody ScenarioCreateDto scenarioCreateDto
    ) {
        return ResponseEntity.ok(scenarioService.createWithoutTransaction(scenarioCreateDto));
    }

    @PostMapping("/with-transaction")
    @Operation(summary = "Run scenario with transaction")
    public ResponseEntity<String> createWithTransaction(
            @Valid @RequestBody ScenarioCreateDto scenarioCreateDto
    ) {
        return ResponseEntity.ok(scenarioService.createWithTransaction(scenarioCreateDto));
    }

    @PostMapping("/with-transaction/async")
    @Operation(summary = "Run scenario with transaction asynchronously")
    public ResponseEntity<ScenarioTaskSubmissionDto> createWithTransactionAsync(
            @Valid @RequestBody ScenarioCreateDto scenarioCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(scenarioAsyncService.createWithTransactionAsync(scenarioCreateDto));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get asynchronous scenario task status")
    public ResponseEntity<ScenarioTaskStatusDto> getTaskStatus(
            @PathVariable
            @Positive(message = "Task id must be positive")
            Long taskId
    ) {
        return ResponseEntity.ok(scenarioAsyncService.getTaskStatus(taskId));
    }

    @GetMapping("/race-condition")
    @Operation(summary = "Demonstrate race condition and thread-safe solutions")
    public ResponseEntity<RaceConditionDemoResultDto> runRaceConditionDemo(
            @RequestParam(defaultValue = "64")
            @Min(value = 50, message = "Thread count must be at least 50")
            int threadCount,
            @RequestParam(defaultValue = "1000")
            @Positive(message = "Increments per thread must be positive")
            int incrementsPerThread
    ) {
        return ResponseEntity.ok(
                raceConditionDemoService.runDemo(threadCount, incrementsPerThread)
        );
    }
}
