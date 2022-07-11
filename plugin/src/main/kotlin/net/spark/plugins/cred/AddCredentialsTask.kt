package net.spark.plugins.cred

import net.spark.plugins.OrderedProperties
import net.spark.plugins.OrderedProperties.Entry
import net.spark.plugins.cred.GradlePropertiesCredentialProvider.commonMasterPassword
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project.GRADLE_PROPERTIES
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION
import java.util.*

abstract class AddCredentialsTask : DefaultTask() {
    @Internal
    @Option(option = "repo", description = "The maven repository name.")
    lateinit var repo: String

    @Internal
    @Option(option = "username", description = "The username for the maven repository.")
    lateinit var username: String

    @Internal
    @Option(option = "password", description = "The password for the maven repository.")
    lateinit var password: String

    @Internal
    @Option(option = "master-password", description = "The master password for the maven repository.")
    var masterPassword: String? = null

    @Internal
    @Option(
        option = "use-repo-master-pass",
        description = "The use the $commonMasterPassword from the gradle.properties file if exist, or set new"
    )
    var useRepoSpecificMasterPassword: Boolean = false

    @TaskAction
    fun perform() {
        val propFilePath = project.gradle.gradleUserHomeDir.resolve(GRADLE_PROPERTIES)
        propFilePath.createNewFile()

        val gradleProperties = OrderedProperties.load(propFilePath)
        val initResult = initializeMasterPassword(gradleProperties)

        gradleProperties.set("${repo}Username", username)
            .set("${repo}Password", Cipher.encrypt(password, initResult.masterPass))

        if (initResult.isNew) {
            val encryptedMasterPass = Cipher.encrypt(initResult.masterPass, SYSTEM_PROPERTY_SEC_LOCATION)
            gradleProperties.set(initResult.name, encryptedMasterPass)
        }

        gradleProperties.save("Updated by gradle task addCredentials")
    }

    private fun initializeMasterPassword(gradleProperties: OrderedProperties): Result {
        return if (useRepoSpecificMasterPassword) {
            getMasterPassword(gradleProperties.getEntry("${repo}MasterPassword"))
        } else {
            getMasterPassword(gradleProperties.getEntry(commonMasterPassword))
        }
    }

    private fun getMasterPassword(masterPassFromFile: Entry): Result {
        validatePassword(masterPassFromFile)
        return Result(
            masterPassFromFile.isEmpty,
            masterPassFromFile.key,
            masterPassFromFile.value.orElse(masterPassword).orUse(UUID.randomUUID().toString())
        )
    }

    /**
     * Performs the following actions
     * 1. Do nothing when master pass from cli or from gradle.properties is not provided as the provided input is used
     * 2. Error out if password from CLI does not match the password from gradle.properties file
     */
    private fun validatePassword(fromPropFile: Entry) {
        if (masterPassword == null || fromPropFile.isEmpty) {
            return
        }

        if (Cipher.decrypt(fromPropFile.value!!, SYSTEM_PROPERTY_SEC_LOCATION) == masterPassword) {
            return
        }

        throw GradleException(
            """
            Password from CLI did not match the password configured(${fromPropFile.key}) in the gradle.properties file.
            Please make sure you are providing the plain text password from the CLI --master-password=PLAIN_TEXT_VALUE
        """.trimIndent()
        )
    }

    /**
     * Return new when current string is null or blank. The new string can be null or blank as well.
     */
    private fun String?.orElse(new: String?): String? {
        if (this.isNullOrBlank()) {
            return new
        }
        return this
    }

    /**
     * Return new when current string is null or blank. The new string can not be null or blank.
     */
    private fun String?.orUse(new: String): String {
        if (this.isNullOrBlank()) {
            return new
        }
        return this
    }

    data class Result(val isNew: Boolean, val name: String, val masterPass: String)
}
