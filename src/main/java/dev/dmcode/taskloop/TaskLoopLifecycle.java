package dev.dmcode.taskloop;

import java.util.concurrent.Future;

public interface TaskLoopLifecycle {

    void start();

    void wakeup();

    Future<Boolean> stop();
}