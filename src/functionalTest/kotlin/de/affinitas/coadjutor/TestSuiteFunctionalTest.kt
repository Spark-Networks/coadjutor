package de.affinitas.coadjutor

import de.affinitas.coadjutor.BuildScriptLanguage.GROOVY
import de.affinitas.coadjutor.BuildScriptLanguage.KOTLIN
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestSuiteFunctionalTest {
    @Test
    fun shouldWorkWithKotlinDSL(@TempDir testDir: File) {
        val builder = TestProjectBuilder.newProject(testDir, KOTLIN).withBuildGradle(
            """
                plugins {
                    id("de.affinitas.coadjutor")
                }

                repositories {
                    mavenCentral()
                }

                coadjutor {
                    test {
                        module("testInt")
                        module("testFunctional", false)
                        module("testAcceptance")
                    }
                }

                val testIntImplementation by configurations.creating
                val testFunctionalImplementation by configurations.creating
                val testAcceptanceImplementation by configurations.creating

                dependencies {
                   testIntImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
                   testFunctionalImplementation("junit:junit:4.13.2")
                   testAcceptanceImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
                }
                """
        ).withJunitPlatformTest("src/testInt/java/de/affinitas/IntTest.java", "IntTest")
            .withJunitTest("src/testFunctional/java/de/affinitas/FunctionalTest.java", "FunctionalTest")
            .withJunitPlatformTest("src/testAcceptance/java/de/affinitas/AcceptanceTest.java", "AcceptanceTest")

        assertThatCode {
            val result = Runner.run(builder, "testInt", "testFunctional", "testAcceptance")
            assertThat(result.task(":testInt")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testFunctional")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testAcceptance")?.outcome).isEqualTo(SUCCESS)
        }.doesNotThrowAnyException()
    }

    @Test
    fun shouldWorkWithGroovyDSL(@TempDir projectRoot: File) {
        val builder = TestProjectBuilder.newProject(projectRoot, GROOVY).withBuildGradle(
            """
                plugins {
                    id("de.affinitas.coadjutor")
                }

                repositories {
                    mavenCentral()
                }

                coadjutor {
                    test { t ->
                        t.module("testInt", false)
                        t.module("testFunctional")
                        t.module("testAcceptance")
                    }
                }

                configurations {
                    testIntImplementation
                    testFunctionalImplementation
                    testAcceptanceImplementation
                }

                dependencies {
                   testIntImplementation("junit:junit:4.13.2")
                   testFunctionalImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
                   testAcceptanceImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
                }
                """
        ).withJunitTest("src/testInt/java/de/affinitas/IntTest.java", "IntTest")
            .withJunitPlatformTest("src/testFunctional/java/de/affinitas/FunctionalTest.java", "FunctionalTest")
            .withJunitPlatformTest("src/testAcceptance/java/de/affinitas/AcceptanceTest.java", "AcceptanceTest")

        assertThatCode {
            val result = Runner.run(builder, "testInt", "testFunctional", "testAcceptance")
            assertThat(result.task(":testInt")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testFunctional")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testAcceptance")?.outcome).isEqualTo(SUCCESS)
        }.doesNotThrowAnyException()
    }

    @Test
    fun shouldSupportModuleDirectory(@TempDir testDir: File) {
        val builder = TestProjectBuilder.newProject(testDir, KOTLIN).withBuildGradle(
            """
                plugins {
                    id("de.affinitas.coadjutor")
                }

                repositories {
                    mavenCentral()
                }

                coadjutor {
                    test {
                        module("testFunctional", false)
                        module("integrationTest", "src/testIntegration")
                    }
                }

                val testFunctionalImplementation by configurations.creating
                val integrationTestImplementation by configurations.creating

                dependencies {
                   testFunctionalImplementation("junit:junit:4.13.2")
                   integrationTestImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
                }
                """
        ).withJunitTest("src/testFunctional/java/de/affinitas/FunctionalTest.java", "FunctionalTest")
            .withJunitPlatformTest("src/testIntegration/java/de/affinitas/IntegrationTest.java", "IntegrationTest")

        assertThatCode {
            val result = Runner.run(builder, "integrationTest", "testFunctional")
            assertThat(result.task(":integrationTest")?.outcome).isEqualTo(SUCCESS)
            assertThat(result.task(":testFunctional")?.outcome).isEqualTo(SUCCESS)

            assertThat(result.output).describedAs("Making sure that test is picked up and executed").contains("FunctionalTest")

            assertThat(result.output).describedAs("Making sure that test is picked up and executed").contains("IntegrationTest")
        }.doesNotThrowAnyException()
    }
}
