package net.spark.coadjutor

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.Project
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

const val commonMasterPassword = "common-master-password"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddCredentialsTaskFunctionalTest {
    private val passwordMatchingRegex = Regex("\\{.*\\}")
    private lateinit var project: Project

    @BeforeAll
    internal fun setUpSuite(@TempDir testDir: File) {
        project = TestProjectBuilder.newProject(testDir, BuildScriptLanguage.KOTLIN)
            .withGradleProperties("")
            .withBuildGradle(
                """
                import java.util.*
                plugins {
                    java
                    id("net.spark.coadjutor")
                }
                """
            )
            .build()
    }

    @AfterEach
    internal fun tearDown() {
        project.gradlePropertiesPath().writeText("")
    }

    @Test
    fun shouldAddRepoCredentialAndRepoMasterPasswordToGradlePropertiesFile() {
        project.gradlePropertiesPath().writeText("")

        val result = Runner.run(
            project,
            "addCredentials",
            "--repo=example",
            "--username=admin",
            "--password=secret",
            "--master-password=example",
            "--use-repo-master-pass"
        )

        assertThat(result.task(":addCredentials")?.outcome).isEqualTo(SUCCESS)
        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(3)
            .containsEntry("exampleUsername", "admin")
            .hasEntrySatisfying("examplePassword") { v -> passwordMatchingRegex.matches(v.toString()) }
            .hasEntrySatisfying("exampleMasterPassword") { v -> passwordMatchingRegex.matches(v.toString()) }
            .doesNotContainKey(commonMasterPassword)
    }

    @Test
    fun shouldNotChangeRepoMasterPasswordOnConflict() {
        project.gradlePropertiesPath().writeText(
            """
            exampleMasterPassword={vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}
        """.trimIndent().trim()
        )

        assertThatCode {
            Runner.run(
                project,
                "addCredentials",
                "--repo=example",
                "--username=admin",
                "--password=secret",
                "--master-password=example",
                "--use-repo-master-pass"
            )
        }.isInstanceOf(UnexpectedBuildFailure::class.java)

        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(1)
            .containsEntry("exampleMasterPassword", "{vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}")
            .doesNotContainKeys("exampleUsername", "examplePassword", commonMasterPassword)
    }

    @Test
    fun shouldUseRepoMasterPasswordWhenExist() {
        project.gradlePropertiesPath().writeText(
            """
            exampleMasterPassword={ikZ/EcySPX0H0CHg74SuF28fmLQTTPhZxW00Y5wyBUc=}
            foo=bar
        """.trimIndent().trim()
        )

        val result = Runner.run(
            project,
            "addCredentials",
            "--repo=example",
            "--username=admin",
            "--password=secret",
            "--use-repo-master-pass"
        )

        assertThat(result.task(":addCredentials")?.outcome).isEqualTo(SUCCESS)
        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(4)
            .containsEntry("foo", "bar")
            .containsEntry("exampleUsername", "admin")
            .hasEntrySatisfying("examplePassword") { v -> passwordMatchingRegex.matches(v.toString()) }
            .containsEntry("exampleMasterPassword", "{ikZ/EcySPX0H0CHg74SuF28fmLQTTPhZxW00Y5wyBUc=}")
            .doesNotContainKey(commonMasterPassword)
    }

    @Test
    fun shouldAddRepoCredentialAndCommonMasterPasswordToGradlePropertiesFile() {
        project.gradlePropertiesPath().writeText("")

        val result = Runner.run(
            project,
            "addCredentials",
            "--repo=example",
            "--username=admin",
            "--password=secret",
            "--master-password=example"
        )

        assertThat(result.task(":addCredentials")?.outcome).isEqualTo(SUCCESS)
        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(3)
            .containsEntry("exampleUsername", "admin")
            .hasEntrySatisfying("examplePassword") { v -> passwordMatchingRegex.matches(v.toString()) }
            .hasEntrySatisfying(commonMasterPassword) { v -> passwordMatchingRegex.matches(v.toString()) }
            .doesNotContainKey("exampleMasterPassword")
    }

    @Test
    fun shouldNotChangeCommonMasterPasswordOnConflict() {
        project.gradlePropertiesPath().writeText(
            """
            $commonMasterPassword={vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}
        """.trimIndent().trim()
        )

        assertThatCode {
            Runner.run(
                project,
                "addCredentials",
                "--repo=example",
                "--username=admin",
                "--password=secret",
                "--master-password=example"
            )
        }.isInstanceOf(UnexpectedBuildFailure::class.java)

        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(1)
            .containsEntry(commonMasterPassword, "{vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}")
            .doesNotContainKeys("exampleUsername", "examplePassword", "exampleMasterPassword")
    }

    @Test
    fun shouldUseCommonMasterPasswordWhenExist() {
        project.gradlePropertiesPath().writeText(
            """
            $commonMasterPassword={ikZ/EcySPX0H0CHg74SuF28fmLQTTPhZxW00Y5wyBUc=}
            foo=bar
        """.trimIndent().trim()
        )

        val result = Runner.run(project, "addCredentials", "--repo=example", "--username=admin", "--password=secret")

        assertThat(result.task(":addCredentials")?.outcome).isEqualTo(SUCCESS)
        assertThat(load(project.gradlePropertiesPath()))
            .hasSize(4)
            .containsEntry("foo", "bar")
            .containsEntry("exampleUsername", "admin")
            .hasEntrySatisfying("examplePassword") { v -> passwordMatchingRegex.matches(v.toString()) }
            .containsEntry(commonMasterPassword, "{ikZ/EcySPX0H0CHg74SuF28fmLQTTPhZxW00Y5wyBUc=}")
            .doesNotContainKey("exampleMasterPassword")
    }

    private infix fun load(path: File): Properties {
        val prop = Properties()
        prop.load(path.inputStream())
        return prop
    }
}
