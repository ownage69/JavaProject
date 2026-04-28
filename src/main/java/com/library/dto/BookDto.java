package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book response DTO")
public class BookDto {

    @Schema(description = "Book identifier", example = "10")
    private Long id;

    @Schema(description = "Book title", example = "1984")
    private String title;

    @Schema(description = "Book ISBN", example = "9780451524935")
    private String isbn;

    @Schema(description = "Book description", example = "A dystopian social science fiction novel.")
    private String description;

    @Schema(description = "Book cover URL or data URL", example = "data:image/webp;base64,...")
    private String coverImageUrl;

    @Schema(description = "Publication year", example = "1949")
    private Integer publishYear;

    @Schema(description = "Number of copies in the library", example = "3")
    private Integer totalCopies;

    @Schema(description = "Currently available copies", example = "2")
    private Integer availableCopies;

    @Schema(description = "Publisher identifier", example = "2")
    private Long publisherId;

    @Schema(description = "Publisher name", example = "Secker & Warburg")
    private String publisherName;

    @Schema(description = "Identifiers of authors", example = "[1, 3]")
    private Set<Long> authorIds;

    @Schema(description = "Authors full names", example = "[\"George Orwell\"]")
    private Set<String> authorNames;

    @Schema(description = "Identifiers of categories", example = "[4, 5]")
    private Set<Long> categoryIds;

    @Schema(description = "Category names", example = "[\"Dystopian\", \"Classic\"]")
    private Set<String> categoryNames;
}
