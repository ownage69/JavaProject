package com.library.service;

import com.library.dto.ScenarioTaskState;
import com.library.dto.ScenarioTaskStatusDto;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Service;

@Service
public class ScenarioTaskRegistryService {

    private final AtomicLong taskIdCounter = new AtomicLong();
    private final AtomicInteger runningTaskCount = new AtomicInteger();
    private final AtomicInteger completedTaskCount = new AtomicInteger();
    private final AtomicInteger failedTaskCount = new AtomicInteger();
    private final ConcurrentMap<Long, ScenarioTaskInfo> tasks = new ConcurrentHashMap<>();

    public Long registerTask() {
        long taskId = taskIdCounter.incrementAndGet();
        tasks.put(taskId, ScenarioTaskInfo.pending());
        return taskId;
    }

    public void markRunning(Long taskId) {
        updateTask(taskId, currentTask -> {
            if (currentTask.isTerminal() || currentTask.getStatus() == ScenarioTaskState.RUNNING) {
                return currentTask;
            }
            runningTaskCount.incrementAndGet();
            return currentTask.running();
        });
    }

    public void markCompleted(Long taskId, String result) {
        updateTask(taskId, currentTask -> {
            if (currentTask.isTerminal()) {
                return currentTask;
            }
            decrementRunningIfNeeded(currentTask);
            completedTaskCount.incrementAndGet();
            return currentTask.completed(result);
        });
    }

    public void markFailed(Long taskId, Throwable throwable) {
        updateTask(taskId, currentTask -> {
            if (currentTask.isTerminal()) {
                return currentTask;
            }
            decrementRunningIfNeeded(currentTask);
            failedTaskCount.incrementAndGet();
            return currentTask.failed(resolveErrorMessage(throwable));
        });
    }

    public ScenarioTaskStatusDto getTaskStatus(Long taskId) {
        ScenarioTaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            throw createTaskNotFoundException(taskId);
        }
        return new ScenarioTaskStatusDto(
                taskId,
                taskInfo.getStatus(),
                taskInfo.getResult(),
                taskInfo.getErrorMessage(),
                taskIdCounter.get(),
                runningTaskCount.get(),
                completedTaskCount.get(),
                failedTaskCount.get()
        );
    }

    private void decrementRunningIfNeeded(ScenarioTaskInfo currentTask) {
        if (currentTask.getStatus() == ScenarioTaskState.RUNNING) {
            runningTaskCount.decrementAndGet();
        }
    }

    private String resolveErrorMessage(Throwable throwable) {
        String message = throwable.getMessage();
        return message == null || message.isBlank()
                ? "Async task failed without details"
                : message;
    }

    private void updateTask(Long taskId, UnaryOperator<ScenarioTaskInfo> updater) {
        tasks.compute(taskId, (id, currentTask) -> {
            if (currentTask == null) {
                throw createTaskNotFoundException(id);
            }
            return updater.apply(currentTask);
        });
    }

    private NoSuchElementException createTaskNotFoundException(Long taskId) {
        return new NoSuchElementException("Task not found with id: " + taskId);
    }
}
