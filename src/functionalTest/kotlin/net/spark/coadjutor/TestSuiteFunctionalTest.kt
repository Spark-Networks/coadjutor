/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package net.spark.coadjutor

import net.spark.coadjutor.BuildScriptLanguage.GROOVY
import net.spark.coadjutor.BuildScriptLanguage.KOTLIN
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestSuiteFunctionalTest {
    @Test
    fun shouldWorkWithKotlinDSL(@TempDir testDir: File) {
        val builder = TestProjectBuilder.newProject(testDir, KOTLIN)
            .withBuildGradle(
                """
                plugins {
                    id("net.spark.plugins")
                }

                repositories {
                    mavenCentral()
                }

                coadjutor {
                    test {
                        module("testInt") { useJunit() }
                        module("testFunctional") { useJunit() }
                        module("testAcceptance") { useJunit() }
                    }
                }

                val testIntImplementation by configurations.creating
                val testFunctionalImplementation by configurations.creating
                val testAcceptanceImplementation by configurations.creating

                dependencies {
                   testIntImplementation("junit:junit:4.13.2")
                   testFunctionalImplementation("junit:junit:4.13.2")
                   testAcceptanceImplementation("junit:junit:4.13.2")
                }
                """
            )

        setupModulesWithTest(builder, "testInt", "testFunctional", "testAcceptance")

        // Run the build
        val runner = GradleRunner.create()
            .withProject(builder.build())
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("testInt", "testFunctional", "testAcceptance")

        assertThatCode {
            val result = runner.build()
            assertThat(result.task(":testInt")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testFunctional")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testAcceptance")?.outcome).isEqualTo(SUCCESS)
        }.doesNotThrowAnyException()
    }

    @Test
    fun shouldWorkWithGroovyDSL(@TempDir projectRoot: File) {
        val builder = TestProjectBuilder.newProject(projectRoot, GROOVY)
            .withBuildGradle(
                """
                plugins {
                    id("net.spark.plugins")
                }

                repositories {
                    mavenCentral()
                }

                coadjutor {
                    test { t ->
                        t.module("testInt") { it.useJunit() }
                        t.module("testFunctional") { it.useJunit() }
                        t.module("testAcceptance") { it.useJunit() }
                    }
                }

                configurations {
                    testIntImplementation
                    testFunctionalImplementation
                    testAcceptanceImplementation
                }

                dependencies {
                   testIntImplementation("junit:junit:4.13.2")
                   testFunctionalImplementation("junit:junit:4.13.2")
                   testAcceptanceImplementation("junit:junit:4.13.2")
                }
                """
            )

        setupModulesWithTest(builder, "testInt", "testFunctional", "testAcceptance")

        // Run the build
        val runner = GradleRunner.create()
            .withProject(builder.build())
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("testInt", "testFunctional", "testAcceptance")

        assertThatCode {
            val result = runner.build()
            assertThat(result.task(":testInt")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testFunctional")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testAcceptance")?.outcome).isEqualTo(SUCCESS)
        }.doesNotThrowAnyException()
    }

    private fun setupModulesWithTest(builder: TestProjectBuilder, vararg modules: String) {
        modules.forEach {
            builder.withFile("src/$it/java/net/spark/ExampleTest.java", exampleTestContent)
        }
    }
}

const val exampleTestContent = """
            package net.spark;

            import org.junit.Test;

            public class ExampleTest {
                @Test
                public void exampleTest() {
                    System.out.println("Example test");
                }
            }

            """

fun GradleRunner.withProject(project: Project): GradleRunner {
    this.withProjectDir(project.projectDir)
    return this
}