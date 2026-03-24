package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Publisher response DTO")
public class PublisherDto {

    @Schema(description = "Publisher identifier", example = "2")
    private Long id;

    @Schema(description = "Publisher name", example = "Secker & Warburg")
    private String name;

    @Schema(description = "Publisher country", example = "United Kingdom")
    private String country;
}
