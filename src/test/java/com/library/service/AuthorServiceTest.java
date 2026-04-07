package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.AuthorCreateDto;
import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void findAllShouldReturnMappedAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(
                createAuthor(1L, "George", "Orwell"),
                createAuthor(2L, "Ray", "Bradbury")
        ));

        List<AuthorDto> result = authorService.findAll();

        assertThat(result)
                .extracting(AuthorDto::getLastName)
                .containsExactly("Orwell", "Bradbury");
    }

    @Test
    void findByIdShouldReturnAuthorDto() {
        when(authorRepository.findById(5L))
                .thenReturn(Optional.of(createAuthor(5L, "Leo", "Tolstoy")));

        AuthorDto result = authorService.findById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getFirstName()).isEqualTo("Leo");
        assertThat(result.getLastName()).isEqualTo("Tolstoy");
    }

    @Test
    void createShouldSaveAuthorAndInvalidateCache() {
        Author savedAuthor = createAuthor(10L, "Jane", "Austen");
        when(authorRepository.save(org.mockito.ArgumentMatchers.any(Author.class)))
                .thenReturn(savedAuthor);

        AuthorDto result = authorService.create(new AuthorCreateDto("Jane", "Austen"));

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getLastName()).isEqualTo("Austen");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void updateShouldModifyExistingAuthorAndInvalidateCache() {
        Author author = createAuthor(3L, "Old", "Name");
        when(authorRepository.findById(3L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);

        AuthorDto result = authorService.update(3L, new AuthorCreateDto("New", "Author"));

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("Author");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldRemoveAuthorAndInvalidateCache() {
        when(authorRepository.existsById(8L)).thenReturn(true);

        authorService.delete(8L);

        verify(authorRepository).deleteById(8L);
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldThrowWhenAuthorMissing() {
        when(authorRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> authorService.delete(9L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Author not found with id: 9");
    }

    private Author createAuthor(Long id, String firstName, String lastName) {
        Author author = new Author();
        author.setId(id);
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return author;
    }
}
