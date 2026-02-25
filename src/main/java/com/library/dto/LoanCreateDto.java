package com.library.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanCreateDto {

    @NotNull(message = "Book id is required")
    private Long bookId;

    @NotNull(message = "Reader id is required")
    private Long readerId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
}
