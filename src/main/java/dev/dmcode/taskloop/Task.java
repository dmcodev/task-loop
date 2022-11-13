package dev.dmcode.taskloop;

import java.time.Duration;
import java.util.Objects;

@FunctionalInterface
public interface Task {

    Result run(Context context) throws Exception;

    record Context(ExecutionOrigin origin) {}

    enum ExecutionOrigin {
        SCHEDULED,
        WOKEN_UP
    }

    sealed interface Result permits DefaultTaskResult, SleepTaskResult {

        static Result ok() {
            return DefaultTaskResult.INSTANCE;
        }

        static Result repeat() {
            return SleepTaskResult.REPEAT_INSTANCE;
        }

        static Result sleep(Duration duration) {
            return new SleepTaskResult(duration);
        }
    }
}

final class DefaultTaskResult implements Task.Result {

    static final DefaultTaskResult INSTANCE = new DefaultTaskResult();
}

record SleepTaskResult(Duration duration) implements Task.Result {

    static final SleepTaskResult REPEAT_INSTANCE = new SleepTaskResult(Duration.ZERO);

    SleepTaskResult {
        Objects.requireNonNull(duration, "Sleep duration must not be null");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Sleep duration must must not be negative");
        }
    }
}
