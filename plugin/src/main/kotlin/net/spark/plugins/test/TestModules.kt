package net.spark.plugins.test

typealias ModuleDSLUnit = TestModules.ModuleDSL.() -> Unit

open class TestModules {
    val modules: MutableSet<Module> = mutableSetOf()

    fun module(name: String, unit: ModuleDSLUnit) {
        val mod = ModuleDSL().apply(unit)
        modules.add(Module(name, mod.enableUnitPlatform))
    }

    open class ModuleDSL {
        var enableUnitPlatform: Boolean = true

        fun useJunitPlatform() {
            enableUnitPlatform = true
        }

        fun useJunit() {
            enableUnitPlatform = false
        }
    }

    data class Module(val name: String, val useJunitPlatform: Boolean)
}