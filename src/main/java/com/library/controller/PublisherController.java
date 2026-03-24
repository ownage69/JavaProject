package com.library.controller;

import com.library.dto.PublisherCreateDto;
import com.library.dto.PublisherDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.PublisherService;
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
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Publishers", description = "Publisher management API")
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
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    @Operation(summary = "Get all publishers")
    public ResponseEntity<List<PublisherDto>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get publisher by id")
    public ResponseEntity<PublisherDto> getPublisherById(
            @PathVariable @Positive(message = "Publisher id must be positive") Long id
    ) {
        return ResponseEntity.ok(publisherService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create publisher")
    public ResponseEntity<PublisherDto> createPublisher(
            @Valid @RequestBody PublisherCreateDto publisherCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.create(publisherCreateDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update publisher")
    public ResponseEntity<PublisherDto> updatePublisher(
            @PathVariable @Positive(message = "Publisher id must be positive") Long id,
            @Valid @RequestBody PublisherCreateDto publisherCreateDto
    ) {
        return ResponseEntity.ok(publisherService.update(id, publisherCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete publisher")
    public ResponseEntity<Void> deletePublisher(
            @PathVariable @Positive(message = "Publisher id must be positive") Long id
    ) {
        publisherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
