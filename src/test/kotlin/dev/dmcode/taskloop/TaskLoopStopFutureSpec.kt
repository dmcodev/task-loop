package dev.dmcode.taskloop

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class TaskLoopStopFutureSpec : StringSpec({

    listOf(
        TaskLoopStopFuture.ThreadJoin(Thread {})
    ).forEach {
        "Should reject negative await timeout in ${it.javaClass.simpleName}" {
            shouldThrow<IllegalArgumentException> { it.await(-1) }
                .shouldHaveMessage("Timeout value must not be negative")
        }
    }

    "Should await thread join" {
        val thread = Thread {
            runCatching { Thread.sleep(10_000) }
        }
        thread.start()
        TaskLoopStopFuture.ThreadJoin(thread).apply {
            await(100) shouldBe false
            thread.interrupt()
            await(5000) shouldBe true
            await(0) shouldBe true
            await() shouldBe true
        }
    }
})