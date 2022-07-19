package de.affinitas.coadjutor.test

open class TestModules {
    val modules: MutableSet<Module> = mutableSetOf()

    fun module(name: String) {
        modules.add(Module(name, "src/$name", true))
    }

    fun module(name: String, useJunitPlatform: Boolean = true) {
        modules.add(Module(name, "src/$name", useJunitPlatform))
    }

    fun module(name: String, dir: String, useJunitPlatform: Boolean = true) {
        modules.add(Module(name, dir, useJunitPlatform))
    }

    data class Module(val name: String, val dir: String, val useJunitPlatform: Boolean)
}
