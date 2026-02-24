package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Authors are required")
    private String authors;

    private String description;

    @Min(value = 1000, message = "Publish year must be at least 1000")
    private Integer publishYear;

    private String categories;
}
