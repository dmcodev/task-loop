package dev.dmcode.taskloop;

import java.util.Optional;

public interface TaskLoopLifecycle {

    void start();

    void wakeup();

    Optional<TaskLoopStopFuture> stop();
}