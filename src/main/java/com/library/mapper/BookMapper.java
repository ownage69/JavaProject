package com.library.mapper;

import com.library.dto.BookCreateDto;
import com.library.dto.BookDto;
import com.library.entity.Book;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private static final int DEFAULT_TOTAL_COPIES = 3;

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        Set<Long> authorIds = book.getAuthors()
                .stream()
                .map(author -> author.getId())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        Set<String> authorNames = book.getAuthors()
                .stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        Set<Long> categoryIds = book.getCategories()
                .stream()
                .map(category -> category.getId())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        Set<String> categoryNames = book.getCategories()
                .stream()
                .map(category -> category.getName())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        int totalCopies = resolveTotalCopies(book);
        int activeLoans = (int) book.getLoans()
                .stream()
                .filter(loan -> !loan.isReturned())
                .count();
        int availableCopies = Math.max(totalCopies - activeLoans, 0);

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getPublishYear(),
                totalCopies,
                availableCopies,
                book.getPublisher().getId(),
                book.getPublisher().getName(),
                authorIds,
                authorNames,
                categoryIds,
                categoryNames
        );
    }

    public Book toEntity(BookCreateDto bookCreateDto) {
        if (bookCreateDto == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(bookCreateDto.getTitle());
        book.setIsbn(bookCreateDto.getIsbn());
        book.setDescription(bookCreateDto.getDescription());
        book.setCoverImageUrl(bookCreateDto.getCoverImageUrl());
        book.setPublishYear(bookCreateDto.getPublishYear());
        book.setTotalCopies(bookCreateDto.getTotalCopies());
        return book;
    }

    public void updateEntityFromDto(BookCreateDto bookCreateDto, Book book) {
        if (bookCreateDto == null || book == null) {
            return;
        }

        book.setTitle(bookCreateDto.getTitle());
        book.setIsbn(bookCreateDto.getIsbn());
        book.setDescription(bookCreateDto.getDescription());
        book.setCoverImageUrl(bookCreateDto.getCoverImageUrl());
        book.setPublishYear(bookCreateDto.getPublishYear());
        book.setTotalCopies(bookCreateDto.getTotalCopies());
    }

    private int resolveTotalCopies(Book book) {
        return book.getTotalCopies() == null || book.getTotalCopies() < 1
                ? DEFAULT_TOTAL_COPIES
                : book.getTotalCopies();
    }
}
