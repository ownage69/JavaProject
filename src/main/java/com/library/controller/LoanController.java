package com.library.controller;

import com.library.dto.LoanCreateDto;
import com.library.dto.LoanDto;
import com.library.exception.ApiErrorResponse;
import com.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Validated
@Tag(name = "Loans", description = "Loan management API")
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
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    @Operation(summary = "Get all loans")
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by id")
    public ResponseEntity<LoanDto> getLoanById(
            @PathVariable @Positive(message = "Loan id must be positive") Long id
    ) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create loan")
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody LoanCreateDto loanCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.create(loanCreateDto));
    }

    @PostMapping("/bulk/without-transaction")
    @Operation(summary = "Create several loans without outer transaction")
    public ResponseEntity<List<LoanDto>> createLoansWithoutTransaction(
            @RequestBody
            @NotEmpty(message = "Loan list must not be empty")
            List<@Valid LoanCreateDto> loanCreateDtos
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createBulkWithoutTransaction(loanCreateDtos));
    }

    @PostMapping("/bulk/with-transaction")
    @Operation(summary = "Create several loans inside one transaction")
    public ResponseEntity<List<LoanDto>> createLoansWithTransaction(
            @RequestBody
            @NotEmpty(message = "Loan list must not be empty")
            List<@Valid LoanCreateDto> loanCreateDtos
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createBulkWithTransaction(loanCreateDtos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update loan")
    public ResponseEntity<LoanDto> updateLoan(
            @PathVariable @Positive(message = "Loan id must be positive") Long id,
            @Valid @RequestBody LoanCreateDto loanCreateDto
    ) {
        return ResponseEntity.ok(loanService.update(id, loanCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete loan")
    public ResponseEntity<Void> deleteLoan(
            @PathVariable @Positive(message = "Loan id must be positive") Long id
    ) {
        loanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
