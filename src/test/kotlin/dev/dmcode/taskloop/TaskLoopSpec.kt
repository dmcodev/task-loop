package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TaskLoopSpec : StringSpec({

    "Should start and stop multiple times" {
        val configuration = TaskLoopConfiguration("test") {}
        TaskLoop(configuration).apply {
            start()
            stop().get().await(5000) shouldBe true
            stop().isPresent shouldBe false
            start()
            stop().get().await(5000) shouldBe true
        }
    }
})