package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a publisher")
public class PublisherCreateDto {

    @NotBlank(message = "Publisher name is required")
    @Size(max = 150, message = "Publisher name must not exceed 150 characters")
    @Schema(description = "Publisher name", example = "Secker & Warburg")
    private String name;

    @NotBlank(message = "Publisher country is required")
    @Size(max = 100, message = "Publisher country must not exceed 100 characters")
    @Schema(description = "Publisher country", example = "United Kingdom")
    private String country;
}
