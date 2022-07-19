package net.spark.coadjutor

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CoadjutorPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("net.spark.coadjutor")

        // Verify the result
        assertNotNull(project.extensions.findByName("coadjutor"))
    }
}
