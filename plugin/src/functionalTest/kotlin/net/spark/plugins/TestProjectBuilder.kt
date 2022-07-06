package net.spark.plugins

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.internal.project.DefaultProject
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.nio.file.Files
import kotlin.io.path.writeText

class TestProjectBuilder private constructor(name: String, projectDir: File) {
    private val project = ProjectBuilder.builder()
        .withProjectDir(projectDir)
        .withName(name)
        .build() as DefaultProject

    companion object {
        fun newProject(projectDir: File, name: String = "sample-project"): TestProjectBuilder {
            return TestProjectBuilder(name, projectDir)
        }
    }

    fun withGroovyBuildGradle(content: String): TestProjectBuilder {
        assertThat(project.rootDir.resolve("build.gradle.kts").exists())
            .describedAs("Kotlin 'build.gradle.kts' script already present at path.")
            .isFalse

        project.rootDir.resolve("build.gradle").writeText(content.trimIndent().trim())
        return this
    }

    fun withKotlinBuildGradle(content: String): TestProjectBuilder {
        assertThat(project.rootDir.resolve("build.gradle").exists())
            .describedAs("Groovy 'build.gradle' script already present at path.")
            .isFalse

        project.rootDir.resolve("build.gradle.kts").writeText(content.trimIndent().trim())
        return this
    }

    fun withFile(path: String, content: String): TestProjectBuilder {
        val filePath = project.rootDir.resolve(path).toPath()
        Files.createDirectories(filePath.parent)
        Files.createFile(filePath).writeText(content.trimIndent().trim())
        return this
    }

    fun build(): Project {
        project.evaluate()
        return project
    }
}