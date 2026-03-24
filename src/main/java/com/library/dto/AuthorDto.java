package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Author response DTO")
public class AuthorDto {

    @Schema(description = "Author identifier", example = "1")
    private Long id;

    @Schema(description = "Author first name", example = "George")
    private String firstName;

    @Schema(description = "Author last name", example = "Orwell")
    private String lastName;
}
