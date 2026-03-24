package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Loan response DTO")
public class LoanDto {

    @Schema(description = "Loan identifier", example = "15")
    private Long id;

    @Schema(description = "Book identifier", example = "10")
    private Long bookId;

    @Schema(description = "Book title", example = "1984")
    private String bookTitle;

    @Schema(description = "Reader identifier", example = "7")
    private Long readerId;

    @Schema(description = "Reader full name", example = "Ivan Petrov")
    private String readerName;

    @Schema(description = "Loan start date", example = "2026-03-24")
    private LocalDate loanDate;

    @Schema(description = "Loan due date", example = "2026-04-10")
    private LocalDate dueDate;

    @Schema(description = "Loan return flag", example = "false")
    private boolean returned;
}
