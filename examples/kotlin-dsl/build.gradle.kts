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
    }
}