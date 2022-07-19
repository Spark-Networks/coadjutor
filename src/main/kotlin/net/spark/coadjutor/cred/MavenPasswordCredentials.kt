package net.spark.coadjutor.cred

import org.gradle.api.credentials.PasswordCredentials


data class MavenPasswordCredentials(private var userName: String?, private var password: String?, var source: String) :
    PasswordCredentials {
    override fun getUsername(): String? {
        return this.userName
    }

    override fun setUsername(userName: String?) {
        this.userName = username
    }

    override fun getPassword(): String? {
        return this.password
    }

    override fun setPassword(password: String?) {
        this.password = password
    }

    override fun toString(): String {
        return String.format("Credentials [username: %s]", userName)
    }
}
