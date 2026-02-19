package com.library.repository;

import com.library.entity.Book;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

    private final Map<UUID, Book> books = new ConcurrentHashMap<>();

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> findById(UUID id) {
        return Optional.ofNullable(books.get(id));
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID());
        }
        books.put(book.getId(), book);
        return book;
    }

    public void deleteById(UUID id) {
        books.remove(id);
    }

    public boolean existsById(UUID id) {
        return books.containsKey(id);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return books.values().stream()
                .filter(book -> isbn.equals(book.getIsbn()))
                .findFirst();
    }

    public List<Book> findByTitleContainingIgnoreCase(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle() != null && 
                        book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> findByAuthorsContainingIgnoreCase(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthors() != null && 
                        book.getAuthors().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> findByTitleContainingIgnoreCaseAndAuthorsContainingIgnoreCase(
            String title, String author) {
        return books.values().stream()
                .filter(book -> book.getTitle() != null && 
                        book.getTitle().toLowerCase().contains(title.toLowerCase()) &&
                        book.getAuthors() != null &&
                        book.getAuthors().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }
}
