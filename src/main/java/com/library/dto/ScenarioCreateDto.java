package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for transaction scenario endpoints")
public class ScenarioCreateDto {

    @NotBlank(message = "Publisher name is required")
    @Size(max = 150, message = "Publisher name must not exceed 150 characters")
    @Schema(description = "Publisher name for generated scenario data", example = "Eksmo")
    private String publisherName;

    @NotBlank(message = "Book title is required")
    @Size(max = 255, message = "Book title must not exceed 255 characters")
    @Schema(description = "Generated book title", example = "War and Peace")
    private String bookTitle;

    @NotBlank(message = "Book isbn is required")
    @Size(max = 32, message = "Book isbn must not exceed 32 characters")
    @Schema(description = "Generated book ISBN", example = "9780140447934")
    private String bookIsbn;

    @NotNull(message = "Author id is required")
    @Positive(message = "Author id must be positive")
    @Schema(description = "Existing author identifier", example = "1")
    private Long authorId;

    @NotNull(message = "Category id is required")
    @Positive(message = "Category id must be positive")
    @Schema(description = "Existing category identifier", example = "4")
    private Long categoryId;

    @NotNull(message = "Reader id is required")
    @Positive(message = "Reader id must be positive")
    @Schema(description = "Existing reader identifier", example = "7")
    private Long readerId;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    @Schema(description = "Loan due date for generated scenario", example = "2026-04-10")
    private LocalDate dueDate;
}
