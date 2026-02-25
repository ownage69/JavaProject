package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherCreateDto {

    @NotBlank(message = "Publisher name is required")
    private String name;

    @NotBlank(message = "Publisher country is required")
    private String country;
}
