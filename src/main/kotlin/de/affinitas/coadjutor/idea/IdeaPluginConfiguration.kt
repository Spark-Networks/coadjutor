package de.affinitas.coadjutor.idea

import de.affinitas.coadjutor.test.TestModules
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.plugins.ide.idea.IdeaPlugin

object IdeaPluginConfiguration {
    fun apply(project: Project, sourceSet: SourceSetContainer, testModules: TestModules) {
        project.plugins.withType(IdeaPlugin::class.java) { idea ->
            val module = idea.model.module

            testModules.modules.forEach { testModule ->
                sourceSet.getByName(testModule.name).let {
                    module.testSourceDirs = module.testSourceDirs + it.allSource.srcDirs
                    module.testResourceDirs = module.testResourceDirs + it.resources.srcDirs
                }
            }
        }
    }
}
