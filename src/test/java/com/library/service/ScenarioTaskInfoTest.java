package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.library.dto.ScenarioTaskState;
import org.junit.jupiter.api.Test;

class ScenarioTaskInfoTest {

    @Test
    void factoryMethodsShouldCreateExpectedStates() {
        ScenarioTaskInfo pending = ScenarioTaskInfo.pending();
        ScenarioTaskInfo running = pending.running();
        ScenarioTaskInfo completed = running.completed("done");
        ScenarioTaskInfo failed = running.failed("boom");

        assertThat(pending.getStatus()).isEqualTo(ScenarioTaskState.PENDING);
        assertThat(pending.isTerminal()).isFalse();

        assertThat(running.getStatus()).isEqualTo(ScenarioTaskState.RUNNING);
        assertThat(running.isTerminal()).isFalse();

        assertThat(completed.getStatus()).isEqualTo(ScenarioTaskState.COMPLETED);
        assertThat(completed.getResult()).isEqualTo("done");
        assertThat(completed.getErrorMessage()).isNull();
        assertThat(completed.isTerminal()).isTrue();

        assertThat(failed.getStatus()).isEqualTo(ScenarioTaskState.FAILED);
        assertThat(failed.getResult()).isNull();
        assertThat(failed.getErrorMessage()).isEqualTo("boom");
        assertThat(failed.isTerminal()).isTrue();
    }
}
