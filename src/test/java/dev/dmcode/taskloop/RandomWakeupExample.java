package dev.dmcode.taskloop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;

@Slf4j
class RandomWakeupExample {

    public static void main(String[] args) throws InterruptedException {
        var configuration = new TaskLoopConfiguration("example", RandomWakeupExample::runTask)
            .withTaskInterval(Duration.ofSeconds(1));
        var taskLoop = new TaskLoop(configuration);
        taskLoop.start();
        new Thread(() -> wakeupTaskLoopRandomly(taskLoop))
            .start();
        Thread.sleep(5000);
        taskLoop.stop();
    }

    private static Task.Result runTask(Task.Context context) {
        if (context.origin() == Task.ExecutionOrigin.SCHEDULED) {
            log.info("Task running as scheduled!");
        }
        if (context.origin() == Task.ExecutionOrigin.WOKEN_UP) {
            log.info("Task running because of the wakeup call!");
        }
        return Task.Result.ok();
    }

    @SneakyThrows
    private static void wakeupTaskLoopRandomly(TaskLoop taskLoop) {
        Thread.sleep(1500 + new Random().nextInt(2000));
        taskLoop.wakeup();
    }
}
