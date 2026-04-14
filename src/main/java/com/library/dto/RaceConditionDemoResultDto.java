package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of race condition demonstration with concurrent counters")
public class RaceConditionDemoResultDto {

    @Schema(description = "Number of threads in ExecutorService", example = "64")
    private int threadCount;

    @Schema(description = "How many increments each thread performs", example = "1000")
    private int incrementsPerThread;

    @Schema(description = "Expected final counter value", example = "64000")
    private int expectedValue;

    @Schema(description = "Result of unsafe counter without synchronization", example = "52391")
    private int unsafeCounterValue;

    @Schema(description = "Result of synchronized counter", example = "64000")
    private int synchronizedCounterValue;

    @Schema(description = "Result of AtomicInteger counter", example = "64000")
    private int atomicCounterValue;

    @Schema(description = "Whether lost updates were observed", example = "true")
    private boolean raceConditionObserved;

    @Schema(description = "How many increments were lost in unsafe counter", example = "11609")
    private int lostUpdates;
}
