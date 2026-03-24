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
@Schema(description = "Payload for creating or updating an author")
public class AuthorCreateDto {

    @NotBlank(message = "Author first name is required")
    @Size(max = 100, message = "Author first name must not exceed 100 characters")
    @Schema(description = "Author first name", example = "George")
    private String firstName;

    @NotBlank(message = "Author last name is required")
    @Size(max = 100, message = "Author last name must not exceed 100 characters")
    @Schema(description = "Author last name", example = "Orwell")
    private String lastName;
}
