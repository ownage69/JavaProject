package com.library.controller;

import com.library.dto.PublisherCreateDto;
import com.library.dto.PublisherDto;
import com.library.service.PublisherService;
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
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherDto>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PublisherDto> createPublisher(
            @Valid @RequestBody PublisherCreateDto publisherCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.create(publisherCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody PublisherCreateDto publisherCreateDto
    ) {
        return ResponseEntity.ok(publisherService.update(id, publisherCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
