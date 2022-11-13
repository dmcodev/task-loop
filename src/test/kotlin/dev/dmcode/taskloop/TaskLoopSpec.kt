package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class TaskLoopSpec : StringSpec({

    "Should start and stop multiple times" {
        val configuration = TaskLoopConfiguration("test") { Task.Result.ok() }
        TaskLoop(configuration).apply {
            start()
            stop().get().await(5000) shouldBe true
            stop().isPresent shouldBe false
            start()
            stop().get().await(5000) shouldBe true
        }
    }

    "Should stop while sleeping between task invocations" {
        val task = Task {
            Task.Result.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofHours(1))
        TaskLoop(configuration).apply {
            start()
            Thread.sleep(100)
            stop().get().await(5000) shouldBe true
        }
    }


    "Should run loop with small task interval" {
        val counterLatch = CountDownLatch(2)
        val task = Task {
            counterLatch.countDown()
            Task.Result.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofMillis(25))
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }

    "Should handle exception" {
        val counterLatch = CountDownLatch(2)
        val exceptionThrown = AtomicBoolean()
        val task = Task {
            counterLatch.countDown()
            if (exceptionThrown.compareAndSet(false, true)) {
                throw RuntimeException("Failure")
            }
            Task.Result.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofMillis(10))
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            exceptionThrown.get() shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }

    "Should run busy loop configured with zero task interval" {
        val counterLatch = CountDownLatch(100)
        val task = Task {
            counterLatch.countDown()
            Task.Result.ok()
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
        val task = Task {
            counterLatch.countDown()
            Task.Result.repeat()
        }
        val configuration = TaskLoopConfiguration("test", task)
        TaskLoop(configuration).apply {
            start()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }

    "Should wakeup" {
        val counterLatch = CountDownLatch(2)
        val task = Task {
            counterLatch.countDown()
            Task.Result.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofHours(1))
        TaskLoop(configuration).apply {
            start()
            Thread.sleep(100)
            wakeup()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }
})