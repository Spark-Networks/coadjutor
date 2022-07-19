import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    idea
    kotlin("jvm") version "1.6.21"
    id("com.gradle.plugin-publish") version "1.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())

    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.sonatype.plexus:plexus-cipher:1.7")
    implementation("org.codehaus.plexus:plexus-sec-dispatcher:2.0")

    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0-M1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.junit-pioneer:junit-pioneer:1.7.1")
    testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:5.13.2")
}

configurations {
    all {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

group = "net.spark.coadjutor"
version = "0.0.2"

pluginBundle {
    website = "https://github.com/Spark-Networks/coadjutor/"
    vcsUrl = "https://github.com/Spark-Networks/coadjutor"
    description = "Coadjutor plugin for gradle"
    tags = listOf("test", "test-modules", "test-logging", "maven", "credentials", "coadjutor")
    pluginTags = mapOf("coadjutor" to listOf("maven", "credentials", "coadjutor", "test", "test-modules", "test-logging"))
}

gradlePlugin {
    plugins {
        create("coadjutor") {
            id = project.group.toString()
            version = project.version
            implementationClass = "net.spark.coadjutor.CoadjutorPlugin"
            displayName = "Coadjutor gradle plugin"
            description = "Plugin to configure test modules and encrypted credentials in gradle.properties file"
        }
    }
}

// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") {}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = FULL
        events(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
