package dev.dmcode.taskloop

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class TaskLoopConfigurationSpec : StringSpec({

    val task = Task { Task.Result.ok() }

    "Should create configuration with default values" {
        TaskLoopConfiguration("test", task).apply {
            name() shouldBe "test"
            task() shouldBe task
            taskInterval() shouldBe Duration.ofSeconds(5)
        }
    }

    "Null constructor parameters should be replaced by default values" {
        TaskLoopConfiguration("test", task) shouldBe
            TaskLoopConfiguration("test", task, null)
    }

    "Should throw on null name" {
        shouldThrow<NullPointerException> {
            TaskLoopConfiguration(null, task)
        }.message shouldBe "Name must not be null"
    }

    "Should throw on null task" {
        shouldThrow<NullPointerException> {
            TaskLoopConfiguration("test", null)
        }.message shouldBe "Task must not be null"
    }

    "Should throw on negative task interval" {
        shouldThrow<IllegalArgumentException> {
            TaskLoopConfiguration("test", task)
                .withTaskInterval(Duration.ofMillis(1).negated())
        }.message shouldBe "Task interval must not be negative"
    }

    "Should build a thread name with increasing thread number" {
        TaskLoopConfiguration("test", task).apply {
            buildThreadName() shouldBe "test-1"
            buildThreadName() shouldBe "test-2"
        }
    }
})
