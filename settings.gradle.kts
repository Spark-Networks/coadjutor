rootProject.name = "spark-gradle-plugins"

pluginManagement {
    includeBuild("./plugin")

    repositories {
        gradlePluginPortal()
    }
}

include("examples:kotlin-dsl")
include("examples:groovy-dsl")
