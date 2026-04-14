package com.library.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.RaceConditionDemoResultDto;
import com.library.dto.ScenarioCreateDto;
import com.library.dto.ScenarioTaskState;
import com.library.dto.ScenarioTaskStatusDto;
import com.library.dto.ScenarioTaskSubmissionDto;
import com.library.service.RaceConditionDemoService;
import com.library.service.ScenarioAsyncService;
import com.library.service.ScenarioService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ScenarioControllerTest {

    @Mock
    private ScenarioService scenarioService;

    @Mock
    private ScenarioAsyncService scenarioAsyncService;

    @Mock
    private RaceConditionDemoService raceConditionDemoService;

    private ScenarioController scenarioController;

    @BeforeEach
    void setUp() {
        scenarioController = new ScenarioController(
                scenarioService,
                scenarioAsyncService,
                raceConditionDemoService
        );
    }

    @Test
    void createWithoutTransactionShouldReturnOkResponse() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(scenarioService.createWithoutTransaction(scenarioCreateDto))
                .thenReturn("Scenario without transaction completed");

        ResponseEntity<String> response =
                scenarioController.createWithoutTransaction(scenarioCreateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Scenario without transaction completed");
        verify(scenarioService).createWithoutTransaction(scenarioCreateDto);
    }

    @Test
    void createWithTransactionShouldReturnOkResponse() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        when(scenarioService.createWithTransaction(scenarioCreateDto))
                .thenReturn("Scenario with transaction completed");

        ResponseEntity<String> response =
                scenarioController.createWithTransaction(scenarioCreateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Scenario with transaction completed");
        verify(scenarioService).createWithTransaction(scenarioCreateDto);
    }

    @Test
    void createWithTransactionAsyncShouldReturnAcceptedResponse() {
        ScenarioCreateDto scenarioCreateDto = createScenarioCreateDto();
        ScenarioTaskSubmissionDto submissionDto = new ScenarioTaskSubmissionDto(
                10L,
                ScenarioTaskState.PENDING
        );
        when(scenarioAsyncService.createWithTransactionAsync(scenarioCreateDto))
                .thenReturn(submissionDto);

        ResponseEntity<ScenarioTaskSubmissionDto> response =
                scenarioController.createWithTransactionAsync(scenarioCreateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(submissionDto);
        verify(scenarioAsyncService).createWithTransactionAsync(scenarioCreateDto);
    }

    @Test
    void getTaskStatusShouldReturnOkResponse() {
        ScenarioTaskStatusDto statusDto = new ScenarioTaskStatusDto(
                11L,
                ScenarioTaskState.RUNNING,
                null,
                null,
                11L,
                1,
                0,
                0
        );
        when(scenarioAsyncService.getTaskStatus(11L)).thenReturn(statusDto);

        ResponseEntity<ScenarioTaskStatusDto> response =
                scenarioController.getTaskStatus(11L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(statusDto);
        verify(scenarioAsyncService).getTaskStatus(11L);
    }

    @Test
    void runRaceConditionDemoShouldReturnOkResponse() {
        RaceConditionDemoResultDto resultDto = new RaceConditionDemoResultDto(
                64,
                1000,
                64000,
                63000,
                64000,
                64000,
                true,
                1000
        );
        when(raceConditionDemoService.runDemo(64, 1000)).thenReturn(resultDto);

        ResponseEntity<RaceConditionDemoResultDto> response =
                scenarioController.runRaceConditionDemo(64, 1000);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(resultDto);
        verify(raceConditionDemoService).runDemo(64, 1000);
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
