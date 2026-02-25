package com.library.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;
    private String title;
    private String isbn;
    private String description;
    private Integer publishYear;
    private Long publisherId;
    private String publisherName;
    private Set<Long> authorIds;
    private Set<String> authorNames;
    private Set<Long> categoryIds;
    private Set<String> categoryNames;
}
