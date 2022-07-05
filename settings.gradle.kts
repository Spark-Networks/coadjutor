rootProject.name = "spark-gradle-plugins"

pluginManagement {
    includeBuild("./plugin")

    repositories {
        gradlePluginPortal()
    }
}

include("sample")
include("groovy-sample")
