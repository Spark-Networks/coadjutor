# Coadjutor gradle plugin

![Tests](https://github.com/Spark-Networks/coadjutor/actions/workflows/gradle.yml/badge.svg)
[![GitHub license](https://img.shields.io/github/license/Spark-Networks/coadjutor)](https://github.com/Spark-Networks/coadjutor/blob/main/LICENSE)
[![Release](https://img.shields.io/github/v/release/Spark-Networks/coadjutor?display_name=release&include_prereleases&sort=date)](https://github.com/Spark-Networks/coadjutor/releases)

One plugin for all gradle project. This is to reduce the boilerplate build script, with following capabilities:

## Encrypted maven repository credentials

Coadjutor plugin supports the reading encrypted maven credentials from the `$GRADLE_USER_HOME/gradle.properties` file.

### Configure maven repositories, and gradle.properties file with encrypted credentials

```groovy
// Example build script with maven repositories

plugins {
    id "net.spark.coadjutor"
}

repositories {
    maven {
        url = uri("https://example-one.maven.repo/example-one/")
    }
    maven {
        url = uri("https://foo.maven.repo/")
        name = "foo"
    }
    maven {
        url = uri("https://bar.maven.repo/")
        name = "bar"
    }
}
```

for above gradle build script you can supply the gradle credentials using one of the following example

```properties
# Example 1: gradle.properties with common master password

common-master-password={dbXfusN/vDQHA3Tq27WyuI9m5dYBcG8PGnDp6m7NyXk\=} # hyphen(-) to avoid possible name collision

fooUsername=picard
fooPassword={jAYUuGmeXdQHX5xt82moXOc+T6pUCHY4qgOrBgwCWU4\=}

barUsername=crusher
barPassword={GamHiV5b48UHvI0S05FRozbbcM2pbxtQ5776MIkUWFc\=}
```

```properties
# Example 2: gradle.properties with repo specific master password

fooUsername=picard
fooPassword={jAYUuGmeXdQHX5xt82moXOc+T6pUCHY4qgOrBgwCWU4\=}
fooMasterPassword={YRav4v49r5wHmqaY2zNcXoVZi6iEBACLgmsB8hrW1Ko\=}

barUsername=crusher
barPassword={FrmAjFKX3tIHxnZcIFwRuTcwT7UA3Ro5OhPOEx+5I9M\=}
barMasterPassword={FjHBiTnS73cHJ18NJi33py27A3LE/gzoVR7Pf5EIhNs\=}
```

### Add credentials to gradle.properties
The plugin is also adds the `addCredentials` task to easily add the credentials to the gradle.properties file.

1. Common master password
    ```bash
    ./gradlew addCredentials --repo=foo \
        --password=s3cr3t \
        --username=picard \
        --master-password="Picard-Epsilon-7-9-3"
    ```
   - Adds following lines to gradle.properties file
   ```properties
   fooUsername=picard
   fooPassword={jAYUuGmeXdQHX5xt82moXOc+T6pUCHY4qgOrBgwCWU4\=}
   fooMasterPassword={YRav4v49r5wHmqaY2zNcXoVZi6iEBACLgmsB8hrW1Ko\=}
   ```
2. Repo specific master password
    ```bash
    ./gradlew addCredentials --repo=foo  \
        --password=s3cr3t \
        --username=picard \
        --master-password="Picard-Epsilon-7-9-3" \
        --use-repo-master-pass
    ```
    - Adds following lines to gradle.properties file
   ```properties
   fooUsername=picard
   fooPassword={jAYUuGmeXdQHX5xt82moXOc+T6pUCHY4qgOrBgwCWU4\=}
   common-master-password={dbXfusN/vDQHA3Tq27WyuI9m5dYBcG8PGnDp6m7NyXk\=} # hyphen(-) to avoid possible name collision
   ```

## Test modules

- Groovy build script `build.gradle`

```groovy
plugins {
    id "net.spark.coadjutor"
}

coadjutor {
    test {
       it.module("testA") // Registers the test module for dir src/testA/java with test task 'testA'
       it.module("testB", false) // Registers the test module for dir src/testB/java with test task 'testB' and uses junit4 test engine
       it.module("testC", "myCustomTestDir") // Registers the test module for dir src/myCustomTestDir/java and uses junit platform test engine
    }
}
```

- Kotlin build script `build.gradle.kts`

```kotlin
plugins {
    id("net.spark.coadjutor")
}

coadjutor {
    test {
        module("testInt")
    }
}
```
