package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reader response DTO")
public class ReaderDto {

    @Schema(description = "Reader identifier", example = "7")
    private Long id;

    @Schema(description = "Reader first name", example = "Ivan")
    private String firstName;

    @Schema(description = "Reader last name", example = "Petrov")
    private String lastName;

    @Schema(description = "Reader email", example = "ivan.petrov@example.com")
    private String email;
}
