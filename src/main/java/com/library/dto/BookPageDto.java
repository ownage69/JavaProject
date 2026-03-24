package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated book response")
public class BookPageDto {

    @Schema(description = "Page content")
    private List<BookDto> content;

    @Schema(description = "Current page number", example = "0")
    private int page;

    @Schema(description = "Page size", example = "5")
    private int size;

    @Schema(description = "Total number of elements", example = "42")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "9")
    private int totalPages;

    @Schema(description = "Query type used to fetch data", example = "jpql")
    private String queryType;
}
