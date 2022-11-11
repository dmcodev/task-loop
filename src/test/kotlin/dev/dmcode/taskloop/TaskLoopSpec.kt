package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TaskLoopSpec : StringSpec({

    "Should start and stop multiple times" {
        val configuration = TaskLoopConfiguration("test") { TaskResult.ok() }
        TaskLoop(configuration).apply {
            start()
            stop().get().await(5000) shouldBe true
            stop().isPresent shouldBe false
            start()
            stop().get().await(5000) shouldBe true
        }
    }

    "Should run loop with small task interval" {
        val counterLatch = CountDownLatch(2)
        val task = Callable {
            counterLatch.countDown()
            TaskResult.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofMillis(25))
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }

    "Should run busy loop configured with zero task interval" {
        val counterLatch = CountDownLatch(100)
        val task = Callable {
            counterLatch.countDown()
            TaskResult.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ZERO)
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }

    "Should run busy loop using repeat result" {
        val counterLatch = CountDownLatch(100)
        val task = Callable {
            counterLatch.countDown()
            TaskResult.repeat()
        }
        val configuration = TaskLoopConfiguration("test", task)
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }
})