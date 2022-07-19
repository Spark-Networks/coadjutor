package de.affinitas.coadjutor.cred

import de.affinitas.coadjutor.OrderedProperties
import org.gradle.api.Project
import org.gradle.api.Project.GRADLE_PROPERTIES
import java.util.*

object GradlePropertiesCredentialProvider {
    /**
     * This is to avoid collision when maven repo is named as common.
     */
    const val commonMasterPassword = "common-master-password"
    private lateinit var gradleProps: OrderedProperties

    /**
     * Returns the maven credentials configured in the gradle.properties file
     *
     */
    fun getCredentials(project: Project, name: String): Optional<MavenPasswordCredentials> {
        val propFile = project.gradle.gradleUserHomeDir.resolve(GRADLE_PROPERTIES)

        if (!propFile.exists() || propFile.isDirectory) {
            project.logger.info("Skipping maven credential plugin: file $propFile does not exists!")
            return Optional.empty()
        }

        gradleProps = OrderedProperties.load(propFile)
        val possibleNames = getNames(name)

        for (repoName in possibleNames) {
            val credentials = resolveFromGradleProperties(project, repoName)
            if (credentials.isPresent) {
                return credentials
            }
        }
        return Optional.empty()
    }

    private fun resolveFromGradleProperties(project: Project, name: String): Optional<MavenPasswordCredentials> {
        val username = gradleProps.getAsOptional("${name}Username")
        val password = gradleProps.getAsOptional("${name}Password")
        val masterPassword = getMasterPassword(project, name)

        if (username.isEmpty || password.isEmpty) {
            return Optional.empty()
        }

        if (masterPassword.isNullOrBlank()) {
            return Optional.of(MavenPasswordCredentials(username.get(), password.get(), GRADLE_PROPERTIES))
        }

        return Optional.of(
            MavenPasswordCredentials(
                username.get(), Cipher.decrypt(password.get(), masterPassword), GRADLE_PROPERTIES
            )
        )
    }

    /**
     * This is to handle gradle auto suffixing the name of repo with digits when it appears multiple times.
     * Ideally, name must not be the same but gradle allows it with hacky solution.
     */
    private fun getNames(nameOrAutoAssignedName: String): List<String> {
        val listOfNames = ArrayList<String>()
        listOfNames.add(nameOrAutoAssignedName)

        if (nameOrAutoAssignedName.contains(Regex("\\d"))) {
            val parts = nameOrAutoAssignedName.split(Regex("\\d"), 2)
            listOfNames.add(parts[0])
        }

        return listOfNames
    }

    /**
     * Returns the master password configured in the gradle.properties file in specified order.
     * 1. Repo specific master password
     * 2. Common master password
     *
     * @return - master password if configured or null
     */
    private fun getMasterPassword(project: Project, name: String): String? {
        val repoSpecificMasterPassword = gradleProps.getAsOptional("${name}MasterPassword")
        if (repoSpecificMasterPassword.isPresent) {
            project.logger.info("Using repo specific master for $name")
            return repoSpecificMasterPassword.get()
        }

        val globalMasterPassword = gradleProps.getAsOptional(commonMasterPassword)
        if (globalMasterPassword.isPresent) {
            project.logger.info("Using common master for $name")
            return globalMasterPassword.get()
        }

        return null
    }
}
