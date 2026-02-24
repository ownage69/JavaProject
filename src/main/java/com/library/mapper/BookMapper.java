package com.library.mapper;

import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getDescription(),
                book.getPublishYear(),
                book.getCategories()
        );
    }

    public Book toEntity(BookCreateDTO bookCreateDTO) {
        if (bookCreateDTO == null) {
            return null;
        }
        return new Book(
                null,
                bookCreateDTO.getTitle(),
                bookCreateDTO.getAuthors(),
                bookCreateDTO.getDescription(),
                bookCreateDTO.getPublishYear(),
                bookCreateDTO.getCategories()
        );
    }

    public void updateEntityFromDTO(BookCreateDTO dto, Book book) {
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
