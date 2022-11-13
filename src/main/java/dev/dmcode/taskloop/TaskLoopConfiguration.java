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

@Value
@Accessors(fluent = true)
public class TaskLoopConfiguration {

    private static final Duration DEFAULT_TASK_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DEFAULT_EXECUTOR_TERMINATION_TIMEOUT = Duration.ofSeconds(60);

    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    AtomicInteger nextThreadNumber = new AtomicInteger(1);

    String name;
    Task task;
    @With
    Duration taskInterval;

    public TaskLoopConfiguration(String name, Task task) {
        this(name, task, null);
    }

    TaskLoopConfiguration(
        String name,
        Task task,
        Duration taskInterval
    ) {
        if (Objects.nonNull(taskInterval) && taskInterval.isNegative()) {
            throw new IllegalArgumentException("Task interval must not be negative");
        }
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.task = Objects.requireNonNull(task, "Task must not be null");
        this.taskInterval = Optional.ofNullable(taskInterval)
            .orElse(DEFAULT_TASK_INTERVAL);
    }

    String buildThreadName() {
        return String.format("%s-%s", name, nextThreadNumber.getAndIncrement());
    }
}
