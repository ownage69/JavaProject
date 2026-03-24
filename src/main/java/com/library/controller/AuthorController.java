package com.library.controller;

import com.library.dto.AuthorCreateDto;
import com.library.dto.AuthorDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.AuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authors", description = "Author management API")
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
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors")
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by id")
    public ResponseEntity<AuthorDto> getAuthorById(
            @PathVariable @Positive(message = "Author id must be positive") Long id
    ) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create author")
    public ResponseEntity<AuthorDto> createAuthor(
            @Valid @RequestBody AuthorCreateDto authorCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.create(authorCreateDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update author")
    public ResponseEntity<AuthorDto> updateAuthor(
            @PathVariable @Positive(message = "Author id must be positive") Long id,
            @Valid @RequestBody AuthorCreateDto authorCreateDto
    ) {
        return ResponseEntity.ok(authorService.update(id, authorCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete author")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable @Positive(message = "Author id must be positive") Long id
    ) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
