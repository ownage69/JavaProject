package com.library.service;

import com.library.dto.BookCreateDto;
import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookDto> findAll() {
        log.debug("Finding all books");
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto findById(UUID id) {
        log.debug("Finding book by id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
        return bookMapper.toDto(book);
    }

    public List<BookDto> searchBooksByAuthor(String author) {
        log.debug("Searching books by author: {}", author);
        if (author != null) {
            return bookRepository.findByAuthorsContainingIgnoreCase(author)
                    .stream()
                    .map(bookMapper::toDto)
                    .toList();
        }
        return findAll();
    }

    public BookDto create(BookCreateDto bookCreateDto) {
        log.debug("Creating new book: {}", bookCreateDto.getTitle());
        Book book = bookMapper.toEntity(bookCreateDto);
        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    public BookDto update(UUID id, BookCreateDto bookCreateDto) {
        log.debug("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
        bookMapper.updateEntityFromDto(bookCreateDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    public void delete(UUID id) {
        log.debug("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
