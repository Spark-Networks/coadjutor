package net.spark.coadjutor

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CoadjutorPluginTest {
    @Test
    fun `plugin must registers extension when applied`() {
        // Given
        val project = ProjectBuilder.builder().build()

        // When
        project.plugins.apply("net.spark.coadjutor")

        // Then
        assertNotNull(project.extensions.findByName("coadjutor"))
    }
}
