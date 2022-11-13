package dev.dmcode.taskloop;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
class SimpleExample {

    public static void main(String[] args) throws InterruptedException {
        var configuration = new TaskLoopConfiguration("example", SimpleExample::runTask)
            .withTaskInterval(Duration.ofSeconds(1));
        var taskLoop = new TaskLoop(configuration);
        taskLoop.start();
        Thread.sleep(5000);
        taskLoop.stop();
    }

    private static Task.Result runTask(Task.Context context) {
        log.info("Task running!");
        return Task.Result.ok();
    }
}
