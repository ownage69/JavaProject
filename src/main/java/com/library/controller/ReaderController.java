package com.library.controller;

import com.library.dto.ReaderCreateDto;
import com.library.dto.ReaderDto;
import com.library.service.ReaderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        return ResponseEntity.ok(readerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        return ResponseEntity.ok(readerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReaderDto> createReader(
            @Valid @RequestBody ReaderCreateDto readerCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readerService.create(readerCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(
            @PathVariable Long id,
            @Valid @RequestBody ReaderCreateDto readerCreateDto
    ) {
        return ResponseEntity.ok(readerService.update(id, readerCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        readerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
