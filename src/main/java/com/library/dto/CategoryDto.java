package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category response DTO")
public class CategoryDto {

    @Schema(description = "Category identifier", example = "4")
    private Long id;

    @Schema(description = "Category name", example = "Science Fiction")
    private String name;
}
