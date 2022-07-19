package de.affinitas.coadjutor

import de.affinitas.coadjutor.cred.MavenRepositoryCredentialConfiguration
import de.affinitas.coadjutor.idea.IdeaPluginConfiguration
import de.affinitas.coadjutor.test.TestModuleConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.plugins.ide.idea.IdeaPlugin
import kotlin.reflect.KClass

class CoadjutorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(ExtensionName, CoadjutorExtension::class.java)

        ensurePluginIsApplied(project, JavaPlugin::class)
        if (project.rootProject.file(".idea").isDirectory) {
            ensurePluginIsApplied(project, IdeaPlugin::class)
        }

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

        project.afterEvaluate {
            TestModuleConfiguration.apply(project, sourceSets, extension.test)
            IdeaPluginConfiguration.apply(project, sourceSets, extension.test)
            MavenRepositoryCredentialConfiguration.apply(project)
        }
    }

    private fun ensurePluginIsApplied(project: Project, kClass: KClass<out Plugin<Project>>) {
        if (!project.plugins.hasPlugin(kClass.java)) {
            project.plugins.apply(kClass.java)
        }
    }

    companion object {
        const val ExtensionName = "coadjutor"
    }
}
