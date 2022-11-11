package dev.dmcode.taskloop;

import java.time.Duration;
import java.util.Objects;

public sealed interface TaskResult permits DefaultTaskResult, SleepTaskResult {

    static TaskResult ok() {
        return DefaultTaskResult.INSTANCE;
    }

    static TaskResult repeat() {
        return SleepTaskResult.REPEAT_INSTANCE;
    }

    static TaskResult sleep(Duration duration) {
        return new SleepTaskResult(duration);
    }
}

final class DefaultTaskResult implements TaskResult {

    static final DefaultTaskResult INSTANCE = new DefaultTaskResult();
}

record SleepTaskResult(Duration duration) implements TaskResult {

    static final SleepTaskResult REPEAT_INSTANCE = new SleepTaskResult(Duration.ZERO);

    SleepTaskResult {
        Objects.requireNonNull(duration, "Sleep duration must not be null");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Sleep duration must must not be negative");
        }
    }
}