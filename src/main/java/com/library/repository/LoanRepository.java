package com.library.repository;

import com.library.entity.Loan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReaderId(Long readerId);

    Optional<Loan> findFirstByBookIdAndReturnedFalse(Long bookId);

    long deleteByBookId(Long bookId);
}
