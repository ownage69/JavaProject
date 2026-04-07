package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.ScenarioCreateDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.entity.Loan;
import com.library.entity.Publisher;
import com.library.entity.Reader;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.LoanRepository;
import com.library.repository.PublisherRepository;
import com.library.repository.ReaderRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ScenarioService scenarioService;

    @Test
    void createWithoutTransactionShouldSavePublisherBookAndLoan() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        Publisher savedPublisher = new Publisher();
        savedPublisher.setId(10L);
        savedPublisher.setName("Demo");
        savedPublisher.setCountry("Россия");
        Book savedBook = new Book();
        savedBook.setId(20L);

        when(publisherRepository.save(any(Publisher.class))).thenReturn(savedPublisher);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(createAuthor(1L)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(createCategory(2L)));
        when(readerRepository.findById(3L)).thenReturn(Optional.of(createReader(3L)));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        String result = scenarioService.createWithoutTransaction(scenarioCreateDto);

        ArgumentCaptor<Publisher> publisherCaptor = ArgumentCaptor.forClass(Publisher.class);
        verify(publisherRepository).save(publisherCaptor.capture());
        assertThat(publisherCaptor.getValue().getName()).startsWith("Demo Publisher-");
        assertThat(publisherCaptor.getValue().getCountry()).isEqualTo("Россия");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        assertThat(bookCaptor.getValue().getPublisher()).isEqualTo(savedPublisher);
        assertThat(bookCaptor.getValue().getAuthors()).hasSize(1);
        assertThat(bookCaptor.getValue().getCategories()).hasSize(1);

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());
        assertThat(loanCaptor.getValue().getBook()).isEqualTo(savedBook);
        assertThat(loanCaptor.getValue().getReader().getId()).isEqualTo(3L);
        assertThat(loanCaptor.getValue().getDueDate()).isEqualTo(scenarioCreateDto.getDueDate());

        assertThat(result).isEqualTo("Scenario without transaction completed");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void createWithTransactionShouldReturnSuccessMessage() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> {
            Publisher publisher = invocation.getArgument(0);
            publisher.setId(100L);
            return publisher;
        });
        when(authorRepository.findById(1L)).thenReturn(Optional.of(createAuthor(1L)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(createCategory(2L)));
        when(readerRepository.findById(3L)).thenReturn(Optional.of(createReader(3L)));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(200L);
            return book;
        });

        String result = scenarioService.createWithTransaction(scenarioCreateDto);

        assertThat(result).isEqualTo("Scenario with transaction completed");
        verify(loanRepository).save(any(Loan.class));
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void createWithoutTransactionShouldThrowWhenAuthorMissing() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(publisherRepository.save(any(Publisher.class))).thenReturn(new Publisher());
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scenarioService.createWithoutTransaction(scenarioCreateDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Author not found with id: 1");
    }

    @Test
    void createWithTransactionShouldThrowWhenCategoryMissing() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(publisherRepository.save(any(Publisher.class))).thenReturn(new Publisher());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(createAuthor(1L)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scenarioService.createWithTransaction(scenarioCreateDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found with id: 2");
    }

    @Test
    void createWithTransactionShouldThrowWhenReaderMissing() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(publisherRepository.save(any(Publisher.class))).thenReturn(new Publisher());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(createAuthor(1L)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(createCategory(2L)));
        when(readerRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scenarioService.createWithTransaction(scenarioCreateDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Reader not found with id: 3");
    }

    private ScenarioCreateDto createScenarioCreateDto() {
        return new ScenarioCreateDto(
                "Demo Publisher",
                "Scenario Book",
                "9780306407000",
                1L,
                2L,
                3L,
                LocalDate.now().plusDays(10)
        );
    }

    private Author createAuthor(Long id) {
        Author author = new Author();
        author.setId(id);
        author.setFirstName("George");
        author.setLastName("Orwell");
        return author;
    }

    private Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("Classic");
        return category;
    }

    private Reader createReader(Long id) {
        Reader reader = new Reader();
        reader.setId(id);
        reader.setFirstName("Ivan");
        reader.setLastName("Petrov");
        reader.setEmail("ivan@example.com");
        return reader;
    }
}
