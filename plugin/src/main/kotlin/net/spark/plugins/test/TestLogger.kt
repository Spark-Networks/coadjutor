package net.spark.plugins.test

import org.gradle.api.internal.tasks.testing.logging.DefaultTestLogging
import org.gradle.api.internal.tasks.testing.logging.ShortExceptionFormatter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

private object Color {
    private const val WhiteBold = "\u001B[0;1m"
    private const val Reset = "\u001B[0m"
    private const val Red = "\u001B[31m"
    private const val Green = "\u001B[32m"
    private const val Yellow = "\u001B[33m"
    private const val White = "\u001B[37m"

    fun whiteBold(vararg s: Any): String {
        return WhiteBold + s.joinToString(" ") + Reset
    }

    fun red(vararg s: Any): String {
        return Red + s.joinToString(" ") + Reset
    }

    fun green(vararg s: Any): String {
        return Green + s.joinToString(" ") + Reset
    }

    fun yellow(vararg s: Any): String {
        return Yellow + s.joinToString(" ") + Reset
    }

    fun white(vararg s: Any): String {
        return White + s.joinToString(" ") + Reset
    }
}

private object Symbol {
    const val CheckMark = "\u2714"
    const val NeutralFace = "\u0CA0_\u0CA0"
    const val xMark = "\u2718"
}

private const val TestRun = "Gradle Test Run"
private const val GradleTestExecutor = "Gradle Test Executor"
private const val DefaultIndent = "    "
private const val ExceptionIndent = "  "

object TestLogger : TestListener {
    override fun beforeSuite(s: TestDescriptor) {
        if (s.name.startsWith(TestRun) || s.name.startsWith(GradleTestExecutor)) return

        if (s.parent != null && s.className != null) {
            println("\n${Color.whiteBold(s.displayName)}")
        }
    }

    override fun afterSuite(s: TestDescriptor, r: TestResult) {
        if (!s.name.startsWith(TestRun)) {
            return
        }

        println("--------------------------------------------------------------------------")
        println(
            "Results: ${coloredResult(r)} (${r.testCount} tests, ${
                Color.green(r.successfulTestCount, "passed")
            }, ${Color.red(r.failedTestCount, "failed")}, ${
                Color.yellow(r.skippedTestCount, "skipped")
            })"
        )
        println("--------------------------------------------------------------------------")
    }

    override fun beforeTest(testDescriptor: TestDescriptor) {
    }

    override fun afterTest(desc: TestDescriptor, r: TestResult) {
        val executionTime = (r.endTime - r.startTime) / 1000.0
        println("${DefaultIndent}${indicator(r)} ${desc.name} (${Color.yellow(executionTime.toString())} secs)")

        r.exceptions?.let {
            if (it.isEmpty()) {
                return
            }

            println(ExceptionIndent + Color.red(ShortExceptionFormatter(DefaultTestLogging()).format(desc, it)))
            println("\n\n")
        }
    }


    private fun indicator(r: TestResult): String {
        return when {
            r.failedTestCount > 0 -> {
                Color.red(Symbol.xMark)
            }
            r.skippedTestCount > 0 -> {
                Color.yellow(Symbol.NeutralFace)
            }
            else -> {
                Color.green(Symbol.CheckMark)
            }
        }
    }

    private fun coloredResult(result: TestResult): String {
        return when (result.resultType) {
            TestResult.ResultType.SUCCESS -> Color.green(result.resultType.name)
            TestResult.ResultType.FAILURE -> Color.red(result.resultType.name)
            else -> Color.white(result.resultType.name)
        }
    }
}
