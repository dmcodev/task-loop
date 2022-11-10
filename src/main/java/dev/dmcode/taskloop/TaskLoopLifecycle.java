package dev.dmcode.taskloop;

public interface TaskLoopLifecycle {

    void start();

    void wakeup();

    boolean stop();
}