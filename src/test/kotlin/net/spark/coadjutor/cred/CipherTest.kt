package net.spark.coadjutor.cred

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.sonatype.plexus.components.cipher.PlexusCipherException


class CipherTest {
    private val encryptedMasterPassword = "{vtm9DrLCtjoHXqLvLglf6x8d24WZ8mZjDWGRjadi1bzP+ILb39hQU5CA86O1C1mC}"
    private val plainTextMasterPassword = "cipher-test-master-password"

    @Test
    fun `decrypt should return the decrypted password using encrypted master password`() {
        val decryptedPassword = Cipher
            .decrypt("{0reIPVKpbmwHdR9S2ggpdF49Vst1VEtqq0nMItmWDds=}", encryptedMasterPassword)

        assertThat(decryptedPassword).isEqualTo("MySup3rS3cr3t")
    }

    @Test
    fun `decrypt should return the decrypted password using plain text master password`() {
        val decryptedPassword = Cipher
            .decrypt("{0reIPVKpbmwHdR9S2ggpdF49Vst1VEtqq0nMItmWDds=}", plainTextMasterPassword)

        assertThat(decryptedPassword).isEqualTo("MySup3rS3cr3t")
    }

    @Test
    fun `decrypt should return plain text password as it is`() {
        val decryptPassword = Cipher.decrypt("MySup3rS3cr3t", encryptedMasterPassword)

        assertThat(decryptPassword).isEqualTo("MySup3rS3cr3t")
    }

    @Test
    fun `decrypt should throw exception when failed to decrypt the password`() {
        assertThatCode { Cipher.decrypt("{not-a-valid-encrypted-password}", encryptedMasterPassword) }
            .isInstanceOf(PlexusCipherException::class.java)
    }

    @Test
    fun `encrypt should encrypt the password using master password`() {
        val gotEncryptedPassword = Cipher.encrypt("MySup3rS3cr3t", encryptedMasterPassword)

        assertThat(gotEncryptedPassword).isNotNull
    }
}
