package com.library.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScenarioAsyncDelayService {

    @Value("${library.scenario.async-delay-millis:10000}")
    private long asyncDelayMillis;

    public void simulateProcessingDelay() {
        if (asyncDelayMillis <= 0) {
            return;
        }
        try {
            log.debug("Simulating async scenario delay for {} ms", asyncDelayMillis);
            Thread.sleep(asyncDelayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Async scenario task was interrupted", exception);
        }
    }
}
