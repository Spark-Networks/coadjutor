package de.affinitas.coadjutor.cred

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

object MavenRepositoryCredentialConfiguration {
    fun apply(project: Project) {
        if (resolveThroughEnvironmentVariables(project)) {
            project.logger.info("Resolved maven credentials from environment variables [$MAVEN_USER, $MAVEN_PASS]")
            return
        }

        if (project.rootProject.tasks.findByPath("addCredentials") == null) {
            project.rootProject.tasks.register("addCredentials", AddCredentialsTask::class.java)
        }

        for (repo in mavenArtifactRepositories(project)) {
            GradlePropertiesCredentialProvider.getCredentials(project, repo.name).ifPresent { cred ->
                repo.credentials {
                    it.username = cred.username
                    it.password = cred.password
                }
            }
        }
    }

    private fun mavenArtifactRepositories(project: Project) = project.repositories
        .filterIsInstance<MavenArtifactRepository>()
        .sortedBy { it.name }

    private fun resolveThroughEnvironmentVariables(project: Project): Boolean {
        val envCreds = EnvironmentVariableCredentialProvider.getCredentials()
        if (envCreds.isPresent) {
            mavenArtifactRepositories(project).forEach { repo ->
                repo.credentials {
                    it.username = envCreds.get().username
                    it.password = envCreds.get().password
                }
            }
            return true
        }
        return false
    }
}
