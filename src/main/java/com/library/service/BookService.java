package com.library.service;

import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookDTO> findAll() {
        log.debug("Finding all books");
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDTO)
                .toList();
    }

    public BookDTO findById(UUID id) {
        log.debug("Finding book by id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
        return bookMapper.toDTO(book);
    }

    public List<BookDTO> searchBooksByAuthor(String author) {
        log.debug("Searching books by author: {}", author);
        if (author != null) {
            return bookRepository.findByAuthorsContainingIgnoreCase(author)
                    .stream()
                    .map(bookMapper::toDTO)
                    .toList();
        } else {
            return findAll();
        }
    }

    public BookDTO create(BookCreateDTO bookCreateDTO) {
        log.debug("Creating new book: {}", bookCreateDTO.getTitle());
        Book book = bookMapper.toEntity(bookCreateDTO);
        Book saved = bookRepository.save(book);
        return bookMapper.toDTO(saved);
    }

    public BookDTO update(UUID id, BookCreateDTO bookCreateDTO) {
        log.debug("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
        bookMapper.updateEntityFromDTO(bookCreateDTO, book);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    public void delete(UUID id) {
        log.debug("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
