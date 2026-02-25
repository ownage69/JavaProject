package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderCreateDto {

    @NotBlank(message = "Reader name is required")
    private String fullName;

    @NotBlank(message = "Reader email is required")
    @Email(message = "Reader email must be valid")
    private String email;
}
