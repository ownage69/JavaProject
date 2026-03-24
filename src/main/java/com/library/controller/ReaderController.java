package com.library.controller;

import com.library.dto.ReaderCreateDto;
import com.library.dto.ReaderDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Readers", description = "Reader management API")
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
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    @Operation(summary = "Get all readers")
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        return ResponseEntity.ok(readerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reader by id")
    public ResponseEntity<ReaderDto> getReaderById(
            @PathVariable @Positive(message = "Reader id must be positive") Long id
    ) {
        return ResponseEntity.ok(readerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create reader")
    public ResponseEntity<ReaderDto> createReader(
            @Valid @RequestBody ReaderCreateDto readerCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readerService.create(readerCreateDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reader")
    public ResponseEntity<ReaderDto> updateReader(
            @PathVariable @Positive(message = "Reader id must be positive") Long id,
            @Valid @RequestBody ReaderCreateDto readerCreateDto
    ) {
        return ResponseEntity.ok(readerService.update(id, readerCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reader")
    public ResponseEntity<Void> deleteReader(
            @PathVariable @Positive(message = "Reader id must be positive") Long id
    ) {
        readerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
