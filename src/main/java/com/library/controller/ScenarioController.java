package com.library.controller;

import com.library.dto.ScenarioCreateDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
