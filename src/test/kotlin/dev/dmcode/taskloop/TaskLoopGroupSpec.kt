package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit

class TaskLoopGroupSpec : StringSpec({

    "Should start and stop multiple times" {
        val configuration = TaskLoopConfiguration("test") { TaskResult.ok() }
        TaskLoopGroup(configuration, 4).apply {
            start()
            stop().get().await(5000) shouldBe true
            stop().isPresent shouldBe false
            start()
            stop().get().await(5000) shouldBe true
        }
    }

    "Should run simple loop group" {
        val groupBarrier = CyclicBarrier(5)
        val threadNames = CopyOnWriteArrayList<String>()
        val task = Callable {
            threadNames.add(Thread.currentThread().name)
            groupBarrier.await()
            TaskResult.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
        TaskLoopGroup(configuration, 4).apply {
            start()
            groupBarrier.await(5, TimeUnit.SECONDS)
            stop().get().await(5000) shouldBe true
            threadNames.toSet() shouldBe setOf("test-1", "test-2", "test-3", "test-4")
        }
    }

    "Should wakeup" {
        val groupBarrier = CyclicBarrier(4)
        val counterLatch = CountDownLatch(2)
        val task = Callable {
            counterLatch.countDown()
            groupBarrier.await()
            TaskResult.ok()
        }
        val configuration = TaskLoopConfiguration("test", task)
            .withTaskInterval(Duration.ofHours(1))
        TaskLoopGroup(configuration, 4).apply {
            start()
            Thread.sleep(100)
            wakeup()
            counterLatch.await(5, TimeUnit.SECONDS) shouldBe true
            stop().get().await(5000) shouldBe true
        }
    }
})