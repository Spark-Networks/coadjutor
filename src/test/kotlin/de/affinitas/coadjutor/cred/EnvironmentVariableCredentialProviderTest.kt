package de.affinitas.coadjutor.cred

import de.affinitas.coadjutor.cred.EnvironmentVariableCredentialProvider
import de.affinitas.coadjutor.cred.MAVEN_PASS
import de.affinitas.coadjutor.cred.MAVEN_USER
import de.affinitas.coadjutor.cred.MavenPasswordCredentials
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetEnvironmentVariable
import org.junitpioneer.jupiter.SetEnvironmentVariable.SetEnvironmentVariables

class EnvironmentVariableCredentialProviderTest {

    @Test
    @SetEnvironmentVariables(
        SetEnvironmentVariable(key = MAVEN_USER, value = "admin"),
        SetEnvironmentVariable(key = MAVEN_PASS, value = "MySup3rS3cr3t")
    )
    internal fun `should return true when environment variables are configured`() {
        val got = EnvironmentVariableCredentialProvider.getCredentials()

        assertThat(got)
            .contains(MavenPasswordCredentials("admin", "MySup3rS3cr3t", "environment variables"))
    }

    @Test
    @SetEnvironmentVariables(
        SetEnvironmentVariable(key = MAVEN_USER, value = ""),
        SetEnvironmentVariable(key = MAVEN_PASS, value = "")
    )
    internal fun `should return false when environment variables are not configured`() {
        val got = EnvironmentVariableCredentialProvider.getCredentials()

        assertThat(got).isEmpty
    }
}
