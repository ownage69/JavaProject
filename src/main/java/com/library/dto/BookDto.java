package com.library.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private UUID id;
    private String title;
    private String authors;
    private String description;
    private Integer publishYear;
    private String categories;
}
