package com.library.service;

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
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public String createWithoutTransaction(ScenarioCreateDto scenarioCreateDto) {
        saveLinkedEntities(scenarioCreateDto);

        if (Boolean.TRUE.equals(scenarioCreateDto.getFail())) {
            throw new IllegalStateException("Scenario failed without transaction");
        }

        return "Scenario without transaction completed";
    }

    @Transactional
    public String createWithTransaction(ScenarioCreateDto scenarioCreateDto) {
        saveLinkedEntities(scenarioCreateDto);

        if (Boolean.TRUE.equals(scenarioCreateDto.getFail())) {
            throw new IllegalStateException("Scenario failed with transaction");
        }

        return "Scenario with transaction completed";
    }

    private void saveLinkedEntities(ScenarioCreateDto scenarioCreateDto) {
        final Publisher publisher = findOrCreatePublisher(scenarioCreateDto.getPublisherName());
        final Author author = findOrCreateAuthor(scenarioCreateDto.getAuthorName());
        final Category category = findOrCreateCategory(scenarioCreateDto.getCategoryName());
        final Reader reader = findOrCreateReader(
                scenarioCreateDto.getReaderName(),
                scenarioCreateDto.getReaderEmail()
        );

        Book book = new Book();
        book.setTitle(scenarioCreateDto.getBookTitle());
        book.setIsbn(scenarioCreateDto.getBookIsbn());
        book.setDescription("Scenario generated book");
        book.setPublishYear(LocalDate.now().getYear());
        book.setPublisher(publisher);
        book.setAuthors(new HashSet<>());
        book.getAuthors().add(author);
        book.setCategories(new HashSet<>());
        book.getCategories().add(category);

        Book savedBook = bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(savedBook);
        loan.setReader(reader);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(scenarioCreateDto.getDueDate());
        loan.setReturned(false);

        loanRepository.save(loan);
    }

    private Publisher findOrCreatePublisher(String publisherName) {
        return publisherRepository.findByNameIgnoreCase(publisherName)
                .orElseGet(() -> {
                    Publisher publisher = new Publisher();
                    publisher.setName(publisherName);
                    publisher.setCountry("Россия");
                    return publisherRepository.save(publisher);
                });
    }

    private Author findOrCreateAuthor(String authorName) {
        return authorRepository.findByFullNameIgnoreCase(authorName)
                .orElseGet(() -> {
                    Author author = new Author();
                    author.setFullName(authorName);
                    return authorRepository.save(author);
                });
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    return categoryRepository.save(category);
                });
    }

    private Reader findOrCreateReader(String readerName, String readerEmail) {
        return readerRepository.findByEmailIgnoreCase(readerEmail)
                .orElseGet(() -> {
                    Reader reader = new Reader();
                    reader.setFullName(readerName);
                    reader.setEmail(readerEmail);
                    return readerRepository.save(reader);
                });
    }
}
