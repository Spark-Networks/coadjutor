package net.spark.coadjutor.cred

import org.gradle.api.GradleException
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher

object Cipher {
    private val cipher: DefaultPlexusCipher = initializeCipher()

    fun decrypt(text: String, masterPassword: String): String {
        if (text.isPlainText()) {
            return text
        }

        return cipher.decryptDecorated(text, getPlaintextMasterPassword(masterPassword))
    }

    fun encrypt(text: String, masterPassword: String): String {
        return cipher.encryptAndDecorate(text, getPlaintextMasterPassword(masterPassword))
    }

    private fun String.isPlainText() = !cipher.isEncryptedString(this)

    private fun getPlaintextMasterPassword(masterPassword: String): String {
        if (masterPassword.isPlainText()) {
            return masterPassword;
        }
        return cipher.decryptDecorated(masterPassword, DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION)
    }

    private fun initializeCipher(): DefaultPlexusCipher {
        try {
            return DefaultPlexusCipher()
        } catch (e: Exception) {
            throw GradleException("Failed to initialize cipher", e)
        }
    }
}
