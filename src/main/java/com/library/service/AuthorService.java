package com.library.service;

import com.library.dto.AuthorCreateDto;
import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(author -> new AuthorDto(author.getId(), author.getFullName()))
                .toList();
    }

    public AuthorDto findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Author not found with id: " + id));
        return new AuthorDto(author.getId(), author.getFullName());
    }

    public AuthorDto create(AuthorCreateDto authorCreateDto) {
        Author author = new Author();
        author.setFullName(authorCreateDto.getFullName());
        Author saved = authorRepository.save(author);
        return new AuthorDto(saved.getId(), saved.getFullName());
    }

    public AuthorDto update(Long id, AuthorCreateDto authorCreateDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Author not found with id: " + id));
        author.setFullName(authorCreateDto.getFullName());
        Author saved = authorRepository.save(author);
        return new AuthorDto(saved.getId(), saved.getFullName());
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NoSuchElementException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}
