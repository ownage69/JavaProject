package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a reader")
public class ReaderCreateDto {

    @NotBlank(message = "Reader first name is required")
    @Size(max = 100, message = "Reader first name must not exceed 100 characters")
    @Schema(description = "Reader first name", example = "Ivan")
    private String firstName;

    @NotBlank(message = "Reader last name is required")
    @Size(max = 100, message = "Reader last name must not exceed 100 characters")
    @Schema(description = "Reader last name", example = "Petrov")
    private String lastName;

    @NotBlank(message = "Reader email is required")
    @Email(message = "Reader email must be valid")
    @Size(max = 150, message = "Reader email must not exceed 150 characters")
    @Schema(description = "Reader email", example = "ivan.petrov@example.com")
    private String email;
}
