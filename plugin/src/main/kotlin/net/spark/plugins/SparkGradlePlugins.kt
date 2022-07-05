package net.spark.plugins

import net.spark.plugins.idea.IdeaPluginConfiguration
import net.spark.plugins.test.TestModuleConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModule
import kotlin.reflect.KClass


class SparkGradlePlugins : Plugin<Project> {
    override fun apply(project: Project) {
        ensurePluginIsApplied(project, JavaPlugin::class)

        if (project.rootProject.file(".idea").isDirectory) {
            ensurePluginIsApplied(project, IdeaPlugin::class)
        }

        val extension = project.extensions.create("spark", SparkPluginExtension::class.java)
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

        project.afterEvaluate {
            TestModuleConfiguration.apply(project, sourceSets, extension.test)
            IdeaPluginConfiguration.apply(project, sourceSets, extension.test)
        }

    }

    private fun ensurePluginIsApplied(project: Project, kClass: KClass<out Plugin<Project>>) {
        if (!project.plugins.hasPlugin(kClass.java)) {
            project.plugins.apply(kClass.java)
        }
    }
}
