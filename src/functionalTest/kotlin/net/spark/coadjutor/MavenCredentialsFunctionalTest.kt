package net.spark.coadjutor

import net.spark.coadjutor.BuildScriptLanguage.KOTLIN
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

class MavenCredentialsFunctionalTest {
    @Test
    fun shouldApplyCredentialsFromGradleProperties(@TempDir testDir: File) {
        val repoName = "affinitas"
        val builder = TestProjectBuilder.newProject(testDir, KOTLIN)
            .withDefaultSettingsGradle()
            .withGradleProperties(
                """
                ${repoName}Username=admin
                ${repoName}Password={0reIPVKpbmwHdR9S2ggpdF49Vst1VEtqq0nMItmWDds=}
                ${repoName}MasterPassword={vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}
                """
            )
            .withBuildGradle(
                """
                import java.util.*
                plugins {
                    java
                    id("net.spark.coadjutor")
                }

                repositories {
                    maven {
                        name = "$repoName"
                        url = uri("http://example/repository/maven-private/")
                    }
                    maven {
                        name = "foo"
                        url = uri("http://example/repository/foo/")
                    }
                    maven {
                        name = "bar"
                        url = uri("http://example/repository/bar/")
                    }
                }

                dependencies {
                   implementation("junit:junit:4.13.2")
                   implementation("foo:foo:123")
                }

                tasks.register("writeRepoConfigAsPropFiles") {
                    project.repositories.filterIsInstance<MavenArtifactRepository>().forEach { repo ->
                        val propFile = project.buildDir.resolve("repos/" + repo.name + ".properties")
                        propFile.parentFile.mkdirs()

                        val prop = Properties()
                        prop.setProperty("name", repo.name)
                        prop.setProperty("url", repo.url.toString())
                        prop.setProperty("username", repo.credentials.username ?: "")
                        prop.setProperty("password", repo.credentials.password ?: "")
                        prop.store(propFile.outputStream(), "")
                    }
                }
                """
            )

        builder.withFile("src/test/java/net/spark/ExampleTest.java", exampleTestContent)

        // Run the build
        val project = builder.build()
        val runner = GradleRunner.create()
            .withProject(project)
            .withPluginClasspath()
            .withTestKitDir(project.gradle.gradleUserHomeDir)
            .forwardOutput()
            .withArguments("writeRepoConfigAsPropFiles")

        assertThatCode {
            val result = runner.build()
            assertThat(result.task(":writeRepoConfigAsPropFiles")?.outcome).isEqualTo(UP_TO_DATE)
        }.doesNotThrowAnyException()


        assertRepo(project, repoName, "http://example/repository/maven-private/", "admin", "MySup3rS3cr3t")
        assertRepo(project, "foo", "http://example/repository/foo/", "", "")
        assertRepo(project, "bar", "http://example/repository/bar/", "", "")
    }

    private fun assertRepo(project: Project, repoName: String, url: String, username: String, password: String) {
        assertThat(project.buildDir.resolve("repos/${repoName}.properties"))
            .exists()

        val properties = Properties()
        properties.load(project.buildDir.resolve("repos/${repoName}.properties").inputStream())
        assertThat(properties)
            .hasSize(4)
            .containsEntry("name", repoName)
            .containsEntry("url", url)
            .containsEntry("username", username)
            .containsEntry("password", password)
    }
}
