package com.library.controller;

import com.library.dto.BookCreateDto;
import com.library.dto.BookDto;
import com.library.dto.BookPageDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
@Tag(name = "Books", description = "Book management API")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Resource not found",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Business or data conflict",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/n-plus-one")
    @Operation(summary = "N+1 demo endpoint")
    public ResponseEntity<List<BookDto>> getBooksWithNplusone() {
        return ResponseEntity.ok(bookService.findAllWithNplusone());
    }

    @GetMapping("/with-entity-graph")
    @Operation(summary = "N+1 fixed endpoint")
    public ResponseEntity<List<BookDto>> getBooksWithEntityGraph() {
        return ResponseEntity.ok(bookService.findAllWithEntityGraph());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookById(
            @PathVariable @Positive(message = "Book id must be positive") Long id
    ) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by author")
    public ResponseEntity<List<BookDto>> searchBooksByAuthor(
            @RequestParam(required = false)
            @Size(max = 100, message = "Author filter must not exceed 100 characters")
            String author
    ) {
        return ResponseEntity.ok(bookService.searchBooksByAuthor(author));
    }

    @GetMapping("/filter/jpql")
    @Operation(summary = "Filter books using JPQL with pagination")
    public ResponseEntity<BookPageDto> filterBooksJpql(
            @RequestParam(required = false)
            @Size(max = 100, message = "Author last name must not exceed 100 characters")
            String authorLastName,
            @RequestParam(required = false)
            @Size(max = 100, message = "Category name must not exceed 100 characters")
            String categoryName,
            @RequestParam(required = false)
            @Size(max = 100, message = "Publisher country must not exceed 100 characters")
            String publisherCountry,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page must be zero or positive")
            int page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be positive")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
    ) {
        return ResponseEntity.ok(bookService.filterBooksJpql(
                authorLastName,
                categoryName,
                publisherCountry,
                page,
                size
        ));
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Filter books using native query with pagination")
    public ResponseEntity<BookPageDto> filterBooksNative(
            @RequestParam(required = false)
            @Size(max = 100, message = "Author last name must not exceed 100 characters")
            String authorLastName,
            @RequestParam(required = false)
            @Size(max = 100, message = "Category name must not exceed 100 characters")
            String categoryName,
            @RequestParam(required = false)
            @Size(max = 100, message = "Publisher country must not exceed 100 characters")
            String publisherCountry,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page must be zero or positive")
            int page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be positive")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
    ) {
        return ResponseEntity.ok(bookService.filterBooksNative(
                authorLastName,
                categoryName,
                publisherCountry,
                page,
                size
        ));
    }

    @PostMapping
    @Operation(summary = "Create new book")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookCreateDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book")
    public ResponseEntity<BookDto> updateBook(
            @PathVariable @Positive(message = "Book id must be positive") Long id,
            @Valid @RequestBody BookCreateDto bookCreateDto
    ) {
        return ResponseEntity.ok(bookService.update(id, bookCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book")
    public ResponseEntity<Void> deleteBook(
            @PathVariable @Positive(message = "Book id must be positive") Long id
    ) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
