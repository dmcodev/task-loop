package dev.dmcode.taskloop

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class TaskLoopConfigurationSpec : StringSpec({

    "Should create configuration with default values" {
        TaskLoopConfiguration("test", {}).apply {
            name() shouldBe "test"
            task().run()
            taskInterval() shouldBe Duration.ofSeconds(5)
            executorTerminationTimeout() shouldBe Duration.ofMinutes(1)
        }
    }

    "Null constructor parameters should be replaced by default values" {
        val task = Runnable {}
        TaskLoopConfiguration("test", task) shouldBe
            TaskLoopConfiguration("test", task, null, null)
    }

    "Should throw on null name" {
        shouldThrow<IllegalArgumentException> {
            TaskLoopConfiguration(null, {})
        }.message shouldBe "Name must not be null"
    }

    "Should throw on null task" {
        shouldThrow<IllegalArgumentException> {
            TaskLoopConfiguration("test", null)
        }.message shouldBe "Task must not be null"
    }

    "Should throw on negative task interval" {
        shouldThrow<IllegalArgumentException> {
            TaskLoopConfiguration("test", {})
                .withTaskInterval(Duration.ofMillis(1).negated())
        }.message shouldBe "Task interval must not be negative"
    }

    "Should throw on negative executor termination timeout" {
        shouldThrow<IllegalArgumentException> {
            TaskLoopConfiguration("test", {})
                .withExecutorTerminationTimeout(Duration.ofMillis(1).negated())
        }.message shouldBe "Executor termination timeout must not be negative"
    }

    "Should build a thread name with increasing thread number" {
        TaskLoopConfiguration("test", {}).apply {
            buildThreadName() shouldBe "test-1"
            buildThreadName() shouldBe "test-2"
        }
    }
})
