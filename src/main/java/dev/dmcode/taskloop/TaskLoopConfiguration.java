package dev.dmcode.taskloop;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@With
@Value
@Accessors(fluent = true)
public class TaskLoopConfiguration {

    private static final Duration DEFAULT_TASK_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DEFAULT_EXECUTOR_TERMINATION_TIMEOUT = Duration.ofSeconds(60);

    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    AtomicInteger nextThreadNumber = new AtomicInteger(1);

    String name;
    Runnable task;
    Duration taskInterval;
    Duration executorTerminationTimeout;

    public TaskLoopConfiguration(String name, Runnable task) {
        this(name, task, null, null);
    }

    public TaskLoopConfiguration(
        String name,
        Runnable task,
        Duration taskInterval,
        Duration executorTerminationTimeout
    ) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Name must not be null");
        }
        if (Objects.isNull(task)) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (Objects.nonNull(taskInterval) && taskInterval.isNegative()) {
            throw new IllegalArgumentException("Task interval must not be negative");
        }
        if (Objects.nonNull(executorTerminationTimeout) && executorTerminationTimeout.isNegative()) {
            throw new IllegalArgumentException("Executor termination timeout must not be negative");
        }
        this.name = name;
        this.task = task;
        this.taskInterval = Optional.ofNullable(taskInterval)
            .orElse(DEFAULT_TASK_INTERVAL);
        this.executorTerminationTimeout = Optional.ofNullable(executorTerminationTimeout)
            .orElse(DEFAULT_EXECUTOR_TERMINATION_TIMEOUT);
    }

    String buildThreadName() {
        return String.format("%s-%s", name, nextThreadNumber.getAndIncrement());
    }
}
