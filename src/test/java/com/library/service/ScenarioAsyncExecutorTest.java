package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.ScenarioCreateDto;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioAsyncExecutorTest {

    @Mock
    private ScenarioService scenarioService;

    @Mock
    private ScenarioTaskRegistryService scenarioTaskRegistryService;

    @Mock
    private ScenarioAsyncDelayService scenarioAsyncDelayService;

    @InjectMocks
    private ScenarioAsyncExecutor scenarioAsyncExecutor;

    @Test
    void createWithTransactionAsyncShouldCompleteTaskSuccessfully() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(scenarioService.createWithTransaction(scenarioCreateDto))
                .thenReturn("Scenario with transaction completed");

        CompletableFuture<String> result = scenarioAsyncExecutor.createWithTransactionAsync(
                5L,
                scenarioCreateDto
        );

        assertThat(result.join()).isEqualTo("Scenario with transaction completed");
        verify(scenarioTaskRegistryService).markRunning(5L);
        verify(scenarioAsyncDelayService).simulateProcessingDelay();
        verify(scenarioTaskRegistryService)
                .markCompleted(5L, "Scenario with transaction completed");
    }

    @Test
    void createWithTransactionAsyncShouldStoreFailureStatus() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        NoSuchElementException exception =
                new NoSuchElementException("Author not found with id: 1");
        when(scenarioService.createWithTransaction(scenarioCreateDto)).thenThrow(exception);

        CompletableFuture<String> result = scenarioAsyncExecutor.createWithTransactionAsync(
                6L,
                scenarioCreateDto
        );

        assertThatThrownBy(result::join)
                .hasCause(exception);
        verify(scenarioTaskRegistryService).markRunning(6L);
        verify(scenarioAsyncDelayService).simulateProcessingDelay();
        verify(scenarioTaskRegistryService).markFailed(6L, exception);
    }

    private ScenarioCreateDto createScenarioCreateDto() {
        return new ScenarioCreateDto(
                "Demo Publisher",
                "Scenario Book",
                "9780306407000",
                1L,
                2L,
                3L,
                LocalDate.now().plusDays(5)
        );
    }
}
