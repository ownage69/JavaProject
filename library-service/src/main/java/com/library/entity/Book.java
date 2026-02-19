package com.library.entity;

import java.util.UUID;

public class Book {

    private UUID id;
    private String title;
    private String authors;
    private String isbn;
    private String description;
    private Integer publishYear;
    private String categories;

    public Book() {
    }

    public Book(UUID id, String title, String authors, String isbn, String description, 
                Integer publishYear, String categories) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.description = description;
        this.publishYear = publishYear;
        this.categories = categories;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
