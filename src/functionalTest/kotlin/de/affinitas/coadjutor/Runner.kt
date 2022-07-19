package de.affinitas.coadjutor

import org.gradle.api.Project
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

object Runner {
    fun run(project: Project, vararg args: String): BuildResult {
        return GradleRunner.create()
            .withPluginClasspath()
            .forwardOutput()
            .withTestKitDir(project.gradle.gradleUserHomeDir)
            .withProject(project)
            .withArguments(*args)
            .build()
    }

    fun run(builder: TestProjectBuilder, vararg args: String): BuildResult {
        return run(builder.build(), *args)
    }

    private fun GradleRunner.withProject(project: Project): GradleRunner {
        this.withProjectDir(project.projectDir)
        return this
    }
}
