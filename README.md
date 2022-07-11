# Spark gradle plugin

One plugin for all gradle project. This is to reduce the boilerplate build script, with following capabilities:

## Test modules

- Groovy build script `build.gradle`

```groovy
plugins {
    id "net.spark.plugins"
}

spark {
    test {
        it.module("testInt") { it.useJunitPlatform() }  // Registers the test module for dir src/testInt/java
    }
}
```

- Kotlin build script `build.gradle.kts`

```kotlin
plugins {
    id("net.spark.plugins")
}

spark {
    test {
        module("testInt") {                           // Registers the test module for dir src/testInt/java
            useJunitPlatform()
        }
    }
}
```
