package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a book")
public class BookCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Book title", example = "1984")
    private String title;

    @NotBlank(message = "Isbn is required")
    @Size(max = 32, message = "Isbn must not exceed 32 characters")
    @Schema(description = "Book ISBN", example = "9780451524935")
    private String isbn;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Book description", example = "A dystopian social science fiction novel.")
    private String description;

    @Min(value = 1000, message = "Publish year must be at least 1000")
    @Schema(description = "Publication year", example = "1949")
    private Integer publishYear;

    @NotNull(message = "Publisher id is required")
    @Positive(message = "Publisher id must be positive")
    @Schema(description = "Publisher identifier", example = "2")
    private Long publisherId;

    @NotEmpty(message = "At least one author id is required")
    @Schema(description = "Identifiers of authors", example = "[1, 3]")
    private Set<@Positive(message = "Author id must be positive") Long> authorIds;

    @NotEmpty(message = "At least one category id is required")
    @Schema(description = "Identifiers of categories", example = "[4, 5]")
    private Set<@Positive(message = "Category id must be positive") Long> categoryIds;
}
