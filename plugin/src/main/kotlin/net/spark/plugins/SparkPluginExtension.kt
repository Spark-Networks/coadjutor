package net.spark.plugins

import net.spark.plugins.test.TestModules
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class SparkPluginExtension @Inject constructor(factory: ObjectFactory) {
    val test = factory.newInstance(TestModules::class.java)

    fun test(unit: TestModules.() -> Unit) {
        test.apply(unit)
    }
}
