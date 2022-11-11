package dev.dmcode.taskloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskLoop implements TaskLoopLifecycle {

    private final Lock lock = new ReentrantLock();

    private final TaskLoopConfiguration configuration;
    private final Logger logger;

    private volatile boolean running;
    private Thread thread;

    public TaskLoop(TaskLoopConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "Configuration must not be null");
        this.logger = LoggerFactory.getLogger(getClass().getName() + "." + configuration.name());
    }

    @Override
    public void start() {
        lock.lock();
        try {
            if (!running) {
                running = true;
                thread = new Thread(this::run, configuration.buildThreadName());
                thread.start();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void wakeup() {

    }

    @Override
    public Optional<TaskLoopStopFuture> stop() {
        Thread stoppingThread = null;
        lock.lock();
        try {
            if (running) {
                running = false;
                thread.interrupt();
                stoppingThread = thread;
                thread = null;
            }
        } finally {
            lock.unlock();
        }
        return Optional.ofNullable(stoppingThread)
            .map(TaskLoopStopFuture.ThreadJoin::new);
    }

    private void run() {
        var task = configuration.task();
        while (running) {
            try {
                task.run();
            } catch (Exception exception) {
                if (running) {
                    logger.error("Task exception", exception);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
