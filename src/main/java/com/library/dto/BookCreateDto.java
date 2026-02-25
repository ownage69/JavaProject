package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Isbn is required")
    private String isbn;

    private String description;

    @Min(value = 1000, message = "Publish year must be at least 1000")
    private Integer publishYear;

    @NotNull(message = "Publisher id is required")
    private Long publisherId;

    @NotEmpty(message = "At least one author id is required")
    private Set<Long> authorIds;

    @NotEmpty(message = "At least one category id is required")
    private Set<Long> categoryIds;
}
