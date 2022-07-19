package de.affinitas.coadjutor.cred;

import java.util.*

const val MAVEN_USER = "MAVEN_USER"
const val MAVEN_PASS = "MAVEN_PASS"

object EnvironmentVariableCredentialProvider {
    fun getCredentials(): Optional<MavenPasswordCredentials> {
        if (isConfigured()) {
            return Optional.of(
                MavenPasswordCredentials(
                    System.getenv(MAVEN_USER),
                    System.getenv(MAVEN_PASS),
                    "environment variables"
                )
            )
        }
        return Optional.empty()
    }

    private fun isConfigured(): Boolean {
        val user = System.getenv(MAVEN_USER)
        val pass = System.getenv(MAVEN_PASS)

        if (user.isNullOrBlank() || pass.isNullOrBlank()) {
            return false
        }
        return true
    }
}
