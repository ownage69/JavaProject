package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioCreateDto {

    @NotBlank(message = "Publisher name is required")
    private String publisherName;

    @NotBlank(message = "Author name is required")
    private String authorName;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotBlank(message = "Book title is required")
    private String bookTitle;

    @NotBlank(message = "Book isbn is required")
    private String bookIsbn;

    @NotBlank(message = "Reader name is required")
    private String readerName;

    @NotBlank(message = "Reader email is required")
    private String readerEmail;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Fail flag is required")
    private Boolean fail;
}
