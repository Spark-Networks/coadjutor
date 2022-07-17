import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
    idea
    `maven-publish`
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
version = "0.0.1"

pluginBundle {
    website = "https://www.spark.net/"
    vcsUrl = "https://gitlab.affinitas.de/bpiprava/spark-gradle-plugins"
    description = "Coadjutor plugin for gradle"
    tags = listOf("spark", "networks", "test", "test-modules", "test-logging", "maven", "credentials")
}

gradlePlugin {
    plugins {
        create("coadjutor") {
            id = project.group.toString()
            version = project.version
            implementationClass = "net.spark.coadjutor.CoadjutorPlugin"
            displayName = "spark-gradle-plugin"
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
