package dev.dmcode.taskloop;

import dev.dmcode.taskloop.Task.ExecutionOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskLoop implements TaskLoopLifecycle {

    private final Lock lock = new ReentrantLock();
    private final Condition wakeupCondition = lock.newCondition();

    private final TaskLoopConfiguration configuration;
    private final Logger logger;

    private volatile boolean running;
    private Thread thread;
    private boolean wakeupRequested;
    private ExecutionOrigin executionOrigin = ExecutionOrigin.SCHEDULED;

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
        lock.lock();
        try {
            if (running) {
                wakeupRequested = true;
                wakeupCondition.signal();
            }
        } finally {
            lock.unlock();
        }
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
                var context = new Task.Context(executionOrigin); // TODO: reduce garbage
                switch (task.run(context)) {
                    case DefaultTaskResult ignored -> sleep(configuration.taskInterval());
                    case SleepTaskResult sleep -> sleep(sleep.duration());
                }
            } catch (Exception exception) {
                if (running) {
                    logger.error("Task invocation exception", exception);
                }
                sleep(configuration.taskInterval());
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sleep(Duration duration) {
        long sleepMilliseconds = duration.toMillis();
        if (sleepMilliseconds == 0) {
            return;
        }
        lock.lock();
        try {
            if (running && !wakeupRequested) {
                wakeupCondition.await(sleepMilliseconds, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException exception) {
            if (running) {
                logger.warn("Sleep between task invocations interrupted", exception);
            }
        } finally {
            executionOrigin = wakeupRequested ? ExecutionOrigin.WOKEN_UP : ExecutionOrigin.SCHEDULED;
            wakeupRequested = false;
            lock.unlock();
        }
    }
}
