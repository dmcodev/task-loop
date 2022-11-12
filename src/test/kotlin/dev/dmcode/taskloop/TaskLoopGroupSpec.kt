package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.Callable
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit

class TaskLoopGroupSpec : StringSpec({

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
})