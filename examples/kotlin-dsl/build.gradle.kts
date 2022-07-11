import java.util.*

plugins {
    java
    id("net.spark.plugins")
}

repositories {
    mavenCentral()
}

val testIntImplementation by configurations.creating

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
    testIntImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
}

spark {
    test {
        module("testInt") {
            useJunitPlatform()
        }

        module("testFunctional") {
            useJunitPlatform()
        }
    }
}


tasks.register("writeRepoConfigAsPropFiles") {
    project.repositories.filterIsInstance<MavenArtifactRepository>().forEach { repo ->
        val propFile = project.buildDir.resolve("repos/" + repo.name + ".properties")
        propFile.parentFile.mkdirs()

        val prop = Properties()
        prop.setProperty("name", repo.name)
        prop.setProperty("url", repo.url.toString())
        prop.setProperty("username", repo.credentials.username ?: "")
        prop.setProperty("password", repo.credentials.password ?: "")
        prop.store(propFile.outputStream(), "")
    }
}