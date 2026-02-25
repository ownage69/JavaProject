package com.library.controller;

import com.library.dto.ScenarioCreateDto;
import com.library.service.ScenarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;

    @PostMapping("/without-transaction")
    public ResponseEntity<String> createWithoutTransaction(
            @Valid @RequestBody ScenarioCreateDto scenarioCreateDto
    ) {
        return ResponseEntity.ok(scenarioService.createWithoutTransaction(scenarioCreateDto));
    }

    @PostMapping("/with-transaction")
    public ResponseEntity<String> createWithTransaction(
            @Valid @RequestBody ScenarioCreateDto scenarioCreateDto
    ) {
        return ResponseEntity.ok(scenarioService.createWithTransaction(scenarioCreateDto));
    }
}
