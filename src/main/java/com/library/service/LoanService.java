package com.library.service;

import com.library.dto.LoanCreateDto;
import com.library.dto.LoanDto;
import com.library.entity.Book;
import com.library.entity.Loan;
import com.library.entity.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final String LOAN_NOT_FOUND_WITH_ID = "Loan not found with id: ";
    private static final String BOOK_NOT_FOUND_WITH_ID = "Book not found with id: ";
    private static final String READER_NOT_FOUND_WITH_ID = "Reader not found with id: ";
    private static final String NO_AVAILABLE_COPY_FOR_BOOK_ID =
            "No available copy remaining for book id: ";

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    @Transactional(readOnly = true)
    public List<LoanDto> findAll() {
        return loanRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoanDto findById(Long id) {
        return loanRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException(LOAN_NOT_FOUND_WITH_ID + id));
    }

    @Transactional
    public LoanDto create(LoanCreateDto loanCreateDto) {
        return createBulkLoans(List.of(loanCreateDto)).get(0);
    }

    public List<LoanDto> createBulkWithoutTransaction(List<LoanCreateDto> loanCreateDtos) {
        return createBulkLoans(loanCreateDtos);
    }

    @Transactional
    public List<LoanDto> createBulkWithTransaction(List<LoanCreateDto> loanCreateDtos) {
        return createBulkLoans(loanCreateDtos);
    }

    @Transactional
    public LoanDto update(Long id, LoanCreateDto loanCreateDto) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(LOAN_NOT_FOUND_WITH_ID + id));

        Book book = bookRepository.findById(loanCreateDto.getBookId())
                .orElseThrow(() -> new NoSuchElementException(
                        BOOK_NOT_FOUND_WITH_ID + loanCreateDto.getBookId()
                ));
        Reader reader = readerRepository.findById(loanCreateDto.getReaderId())
                .orElseThrow(() -> new NoSuchElementException(
                        READER_NOT_FOUND_WITH_ID + loanCreateDto.getReaderId()
                ));

        ensureBookHasAvailableCopy(book, loan);

        loan.setBook(book);
        loan.setReader(reader);
        loan.setDueDate(loanCreateDto.getDueDate());

        Loan saved = loanRepository.save(loan);
        return toDto(saved);
    }

    @Transactional
    public LoanDto returnBook(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(LOAN_NOT_FOUND_WITH_ID + id));

        if (!loan.isReturned()) {
            loan.setReturned(true);
            loan.setReturnDate(LocalDate.now());
        }

        Loan saved = loanRepository.save(loan);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new NoSuchElementException(LOAN_NOT_FOUND_WITH_ID + id);
        }
        loanRepository.deleteById(id);
    }

    private List<LoanDto> createBulkLoans(List<LoanCreateDto> loanCreateDtos) {
        Map<Long, Book> booksById = loadBooksById(loanCreateDtos);
        Map<Long, Reader> readersById = loadReadersById(loanCreateDtos);

        return loanCreateDtos.stream()
                .map(loanCreateDto -> buildLoan(loanCreateDto, booksById, readersById))
                .map(loanRepository::save)
                .map(this::toDto)
                .toList();
    }

    private Loan buildLoan(
            LoanCreateDto loanCreateDto,
            Map<Long, Book> booksById,
            Map<Long, Reader> readersById
    ) {
        Book book = resolveBook(loanCreateDto.getBookId(), booksById);
        Reader reader = resolveReader(loanCreateDto.getReaderId(), readersById);

        ensureBookHasAvailableCopy(book, null);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(loanCreateDto.getDueDate());
        loan.setReturnDate(null);
        loan.setReturned(false);
        return loan;
    }

    private Map<Long, Book> loadBooksById(List<LoanCreateDto> loanCreateDtos) {
        return bookRepository.findAllById(
                        extractDistinctIds(loanCreateDtos, LoanCreateDto::getBookId)
                )
                .stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
    }

    private Map<Long, Reader> loadReadersById(List<LoanCreateDto> loanCreateDtos) {
        return readerRepository.findAllById(
                        extractDistinctIds(loanCreateDtos, LoanCreateDto::getReaderId)
                )
                .stream()
                .collect(Collectors.toMap(Reader::getId, Function.identity()));
    }

    private List<Long> extractDistinctIds(
            List<LoanCreateDto> loanCreateDtos,
            Function<LoanCreateDto, Long> idExtractor
    ) {
        return loanCreateDtos.stream()
                .map(idExtractor)
                .distinct()
                .toList();
    }

    private Book resolveBook(Long bookId, Map<Long, Book> booksById) {
        return Optional.ofNullable(booksById.get(bookId))
                .orElseThrow(() -> new NoSuchElementException(BOOK_NOT_FOUND_WITH_ID + bookId));
    }

    private Reader resolveReader(Long readerId, Map<Long, Reader> readersById) {
        return Optional.ofNullable(readersById.get(readerId))
                .orElseThrow(() -> new NoSuchElementException(READER_NOT_FOUND_WITH_ID + readerId));
    }

    private void ensureBookHasAvailableCopy(Book book, Loan currentLoan) {
        long activeLoans = loanRepository.countByBookIdAndReturnedFalse(book.getId());
        int totalCopies = book.getTotalCopies() == null || book.getTotalCopies() < 1
                ? 3
                : book.getTotalCopies();

        if (currentLoan != null
                && currentLoan.getBook() != null
                && currentLoan.getBook().getId().equals(book.getId())
                && !currentLoan.isReturned()) {
            activeLoans = Math.max(activeLoans - 1, 0);
        }

        if (activeLoans >= totalCopies) {
            throw new IllegalArgumentException(NO_AVAILABLE_COPY_FOR_BOOK_ID + book.getId());
        }
    }

    private LoanDto toDto(Loan loan) {
        String readerName = loan.getReader().getFirstName() + " " + loan.getReader().getLastName();
        return new LoanDto(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getReader().getId(),
                readerName,
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.isReturned()
        );
    }
}
