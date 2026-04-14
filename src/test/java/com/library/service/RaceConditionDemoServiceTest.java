package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.library.dto.RaceConditionDemoResultDto;
import org.junit.jupiter.api.Test;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService raceConditionDemoService =
            new RaceConditionDemoService();

    @Test
    void runDemoShouldShowLostUpdatesForUnsafeCounterAndCorrectSafeCounters() {
        RaceConditionDemoResultDto result = raceConditionDemoService.runDemo(64, 1000);

        assertThat(result.getThreadCount()).isEqualTo(64);
        assertThat(result.getIncrementsPerThread()).isEqualTo(1000);
        assertThat(result.getExpectedValue()).isEqualTo(64000);
        assertThat(result.getUnsafeCounterValue()).isLessThan(result.getExpectedValue());
        assertThat(result.getSynchronizedCounterValue()).isEqualTo(result.getExpectedValue());
        assertThat(result.getAtomicCounterValue()).isEqualTo(result.getExpectedValue());
        assertThat(result.isRaceConditionObserved()).isTrue();
        assertThat(result.getLostUpdates())
                .isEqualTo(result.getExpectedValue() - result.getUnsafeCounterValue());
    }
}
