package net.spark.coadjutor

import net.spark.coadjutor.test.TestModules
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class CoadjutorExtension @Inject constructor(factory: ObjectFactory) {
    val test = factory.newInstance(TestModules::class.java)

    fun test(unit: TestModules.() -> Unit) {
        test.apply(unit)
    }
}
