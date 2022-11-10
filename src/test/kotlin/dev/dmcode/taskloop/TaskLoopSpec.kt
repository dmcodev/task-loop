package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TaskLoopSpec : StringSpec({

    "Should start and stop loop multiple times" {
        val configuration = TaskLoopConfiguration("test") {}
        repeat(3) {
            TaskLoop(configuration).apply {
                start()
                stop() shouldBe true
                stop() shouldBe false
                start()
                stop() shouldBe true
            }
        }
    }
})