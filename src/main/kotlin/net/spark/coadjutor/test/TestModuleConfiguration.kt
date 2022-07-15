package net.spark.coadjutor.test

import org.apache.commons.text.CaseUtils.toCamelCase
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP

internal object TestModuleConfiguration {
    fun apply(project: Project, sourceSets: SourceSetContainer, testModule: TestModules) {
        val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        testModule.modules.forEach { module ->
            project.logger.info("Applying the test module configuration for $module")

            val testSourceSet = sourceSets.create(module.name) {
                it.compileClasspath += main.output
                it.runtimeClasspath += main.output
            }

            listOf("Implementation", "RuntimeOnly").forEach {
                setupConfiguration(project, module.name, it)
            }

            setupTestTask(project, module, testSourceSet)
        }

        project.tasks.withType(Test::class.java) { test ->
            test.addTestListener(TestLogger)

            // To disable the default gradle logging
            test.testLogging {
                it.events = mutableSetOf(STANDARD_ERROR)
            }
        }
    }

    private fun setupTestTask(project: Project, module: TestModules.Module, testSourceSet: SourceSet) {
        val testTask = project.tasks.register(module.name, Test::class.java) {
            it.description = "Runs the ${toCamelCase(module.name, false, ' ')} suite."
            it.group = VERIFICATION_GROUP

            it.testClassesDirs = testSourceSet.output.classesDirs
            it.classpath = testSourceSet.runtimeClasspath

            it.mustRunAfter("test")

            if (module.useJunitPlatform) {
                it.useJUnitPlatform()
            }
        }

        project.tasks.getByName(CHECK_TASK_NAME).dependsOn(testTask.name)
    }

    private fun setupConfiguration(project: Project, module: String, configName: String) {
        project.configurations.maybeCreate("${module}$configName")
        project.configurations.getByName("${module}$configName") {
            it.extendsFrom(project.configurations.getByName(configName.replaceFirstChar { c -> c.lowercase() }))
            it.isVisible = true
            it.isTransitive = true
        }
    }
}
