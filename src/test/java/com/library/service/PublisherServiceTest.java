package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.PublisherCreateDto;
import com.library.dto.PublisherDto;
import com.library.entity.Publisher;
import com.library.repository.PublisherRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void findAllShouldReturnMappedPublishers() {
        when(publisherRepository.findAll()).thenReturn(List.of(
                createPublisher(1L, "Eksmo", "Russia"),
                createPublisher(2L, "Penguin", "United Kingdom")
        ));

        List<PublisherDto> result = publisherService.findAll();

        assertThat(result)
                .extracting(PublisherDto::getName)
                .containsExactly("Eksmo", "Penguin");
    }

    @Test
    void findByIdShouldReturnPublisherDto() {
        when(publisherRepository.findById(3L))
                .thenReturn(Optional.of(createPublisher(3L, "No Starch", "USA")));

        PublisherDto result = publisherService.findById(3L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getCountry()).isEqualTo("USA");
    }

    @Test
    void createShouldSavePublisherAndInvalidateCache() {
        when(publisherRepository.save(any(Publisher.class)))
                .thenReturn(createPublisher(10L, "Packt", "UK"));

        PublisherDto result = publisherService.create(
                new PublisherCreateDto("Packt", "UK")
        );

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Packt");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void updateShouldModifyPublisherAndInvalidateCache() {
        Publisher publisher = createPublisher(11L, "Old", "BY");
        when(publisherRepository.findById(11L)).thenReturn(Optional.of(publisher));
        when(publisherRepository.save(publisher)).thenReturn(publisher);

        PublisherDto result = publisherService.update(
                11L,
                new PublisherCreateDto("New", "PL")
        );

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getCountry()).isEqualTo("PL");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldRemovePublisherAndInvalidateCache() {
        when(publisherRepository.existsById(12L)).thenReturn(true);

        publisherService.delete(12L);

        verify(publisherRepository).deleteById(12L);
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldThrowWhenPublisherMissing() {
        when(publisherRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> publisherService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Publisher not found with id: 99");
    }

    private Publisher createPublisher(Long id, String name, String country) {
        Publisher publisher = new Publisher();
        publisher.setId(id);
        publisher.setName(name);
        publisher.setCountry(country);
        return publisher;
    }
}
