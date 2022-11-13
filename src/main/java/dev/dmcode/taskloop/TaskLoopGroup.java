package dev.dmcode.taskloop;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskLoopGroup implements TaskLoopLifecycle {

    private final Collection<TaskLoop> taskLoops;

    public TaskLoopGroup(TaskLoopConfiguration configuration, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Group size must be bigger than zero");
        }
        this.taskLoops = Stream.generate(() -> new TaskLoop(configuration))
            .limit(size)
            .toList();
    }

    @Override
    public void start() {
        taskLoops.forEach(TaskLoop::start);
    }

    @Override
    public void wakeup() {
        taskLoops.forEach(TaskLoop::wakeup);
    }

    @Override
    public Optional<TaskLoopStopFuture> stop() {
        var futures = taskLoops.stream()
            .map(TaskLoop::stop)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        return futures.size() > 0
            ? Optional.of(new TaskLoopStopFuture.Group(futures))
            : Optional.empty();
    }
}
