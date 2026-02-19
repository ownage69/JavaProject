package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^(?:\\d{10}|\\d{13}|\\d{1,5}-\\d{1,7}-\\d{1,7}-[\\dX])$",
             message = "ISBN must be valid (10 or 13 digits)")
    private String isbn;

    private String description;

    @Min(value = 1000, message = "Publish year must be at least 1000")
    private Integer publishYear;

    private String categories;
}
