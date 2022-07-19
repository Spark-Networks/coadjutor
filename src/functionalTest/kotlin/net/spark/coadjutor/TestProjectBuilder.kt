package net.spark.coadjutor

import org.gradle.api.Project
import org.gradle.api.Project.GRADLE_PROPERTIES
import org.gradle.api.internal.project.DefaultProject
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testfixtures.internal.ProjectBuilderImpl
import java.io.File
import java.nio.file.Files
import kotlin.io.path.writeText

enum class BuildScriptLanguage(val buildFileName: String, val settingsFileName: String) {
    GROOVY("build.gradle", "settings.gradle"),
    KOTLIN("build.gradle.kts", "settings.gradle.kts")
}

class TestProjectBuilder private constructor(
    name: String,
    projectDir: File,
    private val scriptLanguage: BuildScriptLanguage = BuildScriptLanguage.KOTLIN
) : ProjectBuilderImpl() {
    private val project = ProjectBuilder.builder()
        .withProjectDir(projectDir)
        .withName(name)
        .build() as DefaultProject

    companion object {
        fun newProject(
            projectParentDir: File,
            scriptLanguage: BuildScriptLanguage,
            name: String = "sample-project"
        ): TestProjectBuilder {
            return TestProjectBuilder(name, projectParentDir.ensureChildDir(name), scriptLanguage)
        }
    }

    fun withBuildGradle(content: String): TestProjectBuilder {
        return withFile(scriptLanguage.buildFileName, content)
    }

    fun withSettingsGradle(content: String): TestProjectBuilder {
        return withFile(scriptLanguage.settingsFileName, content)
    }

    fun withDefaultSettingsGradle(): TestProjectBuilder {
        return withFile(scriptLanguage.settingsFileName, """rootProject.name = "${project.name}" """)
    }

    fun withFile(path: String, content: String): TestProjectBuilder {
        val filePath = project.rootDir.resolve(path).toPath()
        Files.createDirectories(filePath.parent)
        Files.createFile(filePath).writeText(content.trimIndent().trim())
        return this
    }

    fun withGradleProperties(content: String): TestProjectBuilder {
        return withFile("${project.gradle.gradleUserHomeDir}/$GRADLE_PROPERTIES", content)
    }

    fun withJunitTest(path: String, name: String, content: String = "System.out.println(\"Example test\");"): TestProjectBuilder {
        return withFile(
            path,
            """
                package net.spark;

                import org.junit.Test;

                public class $name {
                    @Test
                    public void exampleTest() {
                        $content
                    }
                }
                """
        )
    }

    fun withJunitPlatformTest(path: String, name: String, content: String = "System.out.println(\"Example test\");"): TestProjectBuilder {
        return withFile(
            path,
            """
            package net.spark;

            import org.junit.jupiter.api.Test;

            public class $name {
                @Test
                public void exampleTest() {
                    $content
                }
            }
            """
        )
    }

    fun build(): Project {
        return project.evaluate()
    }
}

fun Project.gradlePropertiesPath(): File {
    return gradle.gradleUserHomeDir.resolve(GRADLE_PROPERTIES)
}

private fun File.ensureChildDir(name: String): File {
    val child = this.resolve(name)
    if (child.isDirectory) {
        return child
    }

    if (child.mkdirs()) {
        return child
    }

    throw RuntimeException("failed to create child dir $name under $this")
}
