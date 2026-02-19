package com.library.mapper;

import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toDTO(Book book);

    Book toEntity(BookCreateDTO bookCreateDTO);

    void updateEntityFromDTO(BookCreateDTO dto, @MappingTarget Book book);
}
