package com.library.mapper;

import com.library.dto.BookCreateDto;
import com.library.dto.BookDto;
import com.library.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getDescription(),
                book.getPublishYear(),
                book.getCategories()
        );
    }

    public Book toEntity(BookCreateDto bookCreateDto) {
        if (bookCreateDto == null) {
            return null;
        }
        return new Book(
                null,
                bookCreateDto.getTitle(),
                bookCreateDto.getAuthors(),
                bookCreateDto.getDescription(),
                bookCreateDto.getPublishYear(),
                bookCreateDto.getCategories()
        );
    }

    public void updateEntityFromDto(BookCreateDto dto, Book book) {
        if (dto == null || book == null) {
            return;
        }
        book.setTitle(dto.getTitle());
        book.setAuthors(dto.getAuthors());
        book.setDescription(dto.getDescription());
        book.setPublishYear(dto.getPublishYear());
        book.setCategories(dto.getCategories());
    }
}
