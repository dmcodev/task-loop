package dev.dmcode.taskloop

import io.kotest.core.spec.style.StringSpec
import kotlin.reflect.KClass

class ExamplesSpec : StringSpec({

    val enabled = System.getProperty("run.examples.test").toBoolean()

    listOf<KClass<*>>(
        SimpleExample::class,
        RandomWakeupExample::class
    ).forEach {
        "Example should run and complete: ${it.simpleName}".config(enabled = enabled) {
            it.java.getMethod("main", arrayOf<String>()::class.java)
                .invoke(null, arrayOf<String>())
        }
    }
})