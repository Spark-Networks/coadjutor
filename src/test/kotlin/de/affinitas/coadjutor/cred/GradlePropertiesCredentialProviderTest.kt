package de.affinitas.coadjutor.cred

import de.affinitas.coadjutor.cred.GradlePropertiesCredentialProvider.commonMasterPassword
import de.affinitas.coadjutor.cred.GradlePropertiesCredentialProvider.getCredentials
import de.affinitas.coadjutor.cred.MavenPasswordCredentials
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.Project.GRADLE_PROPERTIES
import org.gradle.api.invocation.Gradle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

class GradlePropertiesCredentialProviderTest {
    private val username = "admin"
    private val plainTextPassword = "MySup3rS3cr3t"
    private val source = "gradle.properties"
    private val masterPassword = "{vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}"
    private val encryptedUserPassword = "{0reIPVKpbmwHdR9S2ggpdF49Vst1VEtqq0nMItmWDds=}"

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var gradle: Gradle

    private lateinit var gradlePropFile: File


    @BeforeEach
    internal fun setUp(@TempDir tmpdir: File) {
        openMocks(this)
        whenever(project.gradle).thenReturn(gradle)
        whenever(project.logger).thenReturn(mock())

        val gradleUserHome = tmpdir.resolve("userHome/.gradle")
        gradleUserHome.mkdirs()
        gradlePropFile = gradleUserHome.resolve(GRADLE_PROPERTIES)
        whenever(gradle.gradleUserHomeDir).thenReturn(gradleUserHome)
    }

    @Test
    fun `should return empty when gradle properties are not configured for the given repo name`() {
        val repoName = "affinitas"
        withGradleProperties("")

        val gotCredentials = getCredentials(project, repoName)

        assertThat(gotCredentials).isNotNull
        assertThat(gotCredentials).isEmpty
    }

    @Test
    fun `should return credential as it is when there is no master password configured`() {
        val repoName = "affinitas"
        withGradleProperties(
            """
            ${repoName}Username=$username
            ${repoName}Password=$plainTextPassword
        """
        )

        val gotCredentials = getCredentials(project, repoName)

        assertThat(gotCredentials).isNotNull
        assertThat(gotCredentials.get()).isEqualTo(
            MavenPasswordCredentials(
                username,
                plainTextPassword,
                source
            )
        )
    }

    @Test
    fun `should return decrypted creds using repo specific password`() {
        val repoName = "affinitas"
        withGradleProperties(
            """
            ${repoName}Username=$username
            ${repoName}Password=$encryptedUserPassword
            ${repoName}MasterPassword=$masterPassword
        """
        )

        val gotCredentials = getCredentials(project, repoName)

        assertThat(gotCredentials).isNotNull
        assertThat(gotCredentials.get()).isEqualTo(
            MavenPasswordCredentials(
                username,
                plainTextPassword,
                source
            )
        )
    }

    @Test
    fun `should return decrypted creds using common password`() {
        val repoName = "affinitas"
        withGradleProperties(
            """
            ${repoName}Username=$username
            ${repoName}Password=$encryptedUserPassword
            $commonMasterPassword=$masterPassword
        """
        )

        val gotCredentials = getCredentials(project, repoName)

        assertThat(gotCredentials).isNotNull
        assertThat(gotCredentials.get()).isEqualTo(
            MavenPasswordCredentials(
                username,
                plainTextPassword,
                source
            )
        )
    }

    @Test
    fun `should return credential for gradle generated repo names`() {
        val repoNameInGradleProperties = "affinitas"
        val generatedRepoNameByGradle = "affinitas1"
        withGradleProperties(
            """
            ${repoNameInGradleProperties}Username=$username
            ${repoNameInGradleProperties}Password=$encryptedUserPassword
            ${repoNameInGradleProperties}MasterPassword=$masterPassword
        """
        )

        val gotCredentials = getCredentials(project, generatedRepoNameByGradle)

        assertThat(gotCredentials).isNotNull
        assertThat(gotCredentials.get()).isEqualTo(
            MavenPasswordCredentials(
                username,
                plainTextPassword,
                source
            )
        )
    }

    private fun withGradleProperties(content: String) {
        gradlePropFile.writeText(content.trimIndent().trim())
    }
}
