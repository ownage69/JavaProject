package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.ReaderCreateDto;
import com.library.dto.ReaderDto;
import com.library.entity.Reader;
import com.library.repository.ReaderRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private ReaderService readerService;

    @Test
    void findAllShouldReturnMappedReaders() {
        when(readerRepository.findAll()).thenReturn(List.of(
                createReader(1L, "Ivan", "Petrov", "ivan@example.com"),
                createReader(2L, "Anna", "Sidorova", "anna@example.com")
        ));

        assertThat(readerService.findAll())
                .extracting(ReaderDto::getEmail)
                .containsExactly("ivan@example.com", "anna@example.com");
    }

    @Test
    void findByIdShouldReturnReaderDto() {
        Reader reader = createReader(10L, "Ivan", "Petrov", "ivan.petrov@example.com");
        when(readerRepository.findById(10L)).thenReturn(Optional.of(reader));

        ReaderDto result = readerService.findById(10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getFirstName()).isEqualTo("Ivan");
        assertThat(result.getLastName()).isEqualTo("Petrov");
        assertThat(result.getEmail()).isEqualTo("ivan.petrov@example.com");
    }

    @Test
    void createShouldPersistReader() {
        ReaderCreateDto createDto = new ReaderCreateDto("Anna", "Smirnova", "anna@example.com");
        Reader savedReader = createReader(20L, "Anna", "Smirnova", "anna@example.com");
        when(readerRepository.save(any(Reader.class))).thenReturn(savedReader);

        ReaderDto result = readerService.create(createDto);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getFirstName()).isEqualTo("Anna");
        assertThat(result.getEmail()).isEqualTo("anna@example.com");
    }

    @Test
    void updateShouldModifyReader() {
        Reader reader = createReader(30L, "Old", "Name", "old@example.com");
        ReaderCreateDto updateDto = new ReaderCreateDto("New", "Reader", "new@example.com");
        when(readerRepository.findById(30L)).thenReturn(Optional.of(reader));
        when(readerRepository.save(reader)).thenReturn(reader);

        ReaderDto result = readerService.update(30L, updateDto);

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("Reader");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void deleteShouldRemoveReaderWhenItExists() {
        when(readerRepository.existsById(40L)).thenReturn(true);

        readerService.delete(40L);

        verify(readerRepository).deleteById(40L);
    }

    @Test
    void deleteShouldThrowWhenReaderDoesNotExist() {
        when(readerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> readerService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Reader not found with id: 99");

        verify(readerRepository).existsById(99L);
    }

    private Reader createReader(Long id, String firstName, String lastName, String email) {
        Reader reader = new Reader();
        reader.setId(id);
        reader.setFirstName(firstName);
        reader.setLastName(lastName);
        reader.setEmail(email);
        return reader;
    }
}
