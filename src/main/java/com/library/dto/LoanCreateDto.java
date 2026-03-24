package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a loan")
public class LoanCreateDto {

    @NotNull(message = "Book id is required")
    @Positive(message = "Book id must be positive")
    @Schema(description = "Book identifier", example = "10")
    private Long bookId;

    @NotNull(message = "Reader id is required")
    @Positive(message = "Reader id must be positive")
    @Schema(description = "Reader identifier", example = "7")
    private Long readerId;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    @Schema(description = "Loan due date", example = "2026-04-10")
    private LocalDate dueDate;
}
