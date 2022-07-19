package net.spark.coadjutor.test

open class TestModules {
    val modules: MutableSet<Module> = mutableSetOf()

    /**
     * Register a test module with name with following defaults (e.g. module name is testInt)
     * 1. Test engine - Junit platform(Junit 5)
     * 2. Test dir - src/testInt
     * 3. Test task name - testInt
     * 4. Test configuration - testIntImplementation, testIntRuntimeOnly
     */
    fun module(name: String) {
        modules.add(Module(name, "src/$name", true))
    }

    /**
     * Register a test module with name and junit test engine, with following defaults (e.g. module name is testInt)
     * 1. Test engine - Junit platform(Junit 5) if true or else junit4. Defaults to junit5
     * 2. Test dir - src/testInt
     * 3. Test task name - testInt
     * 4. Test configuration - testIntImplementation, testIntRuntimeOnly
     */
    fun module(name: String, useJunitPlatform: Boolean = true) {
        modules.add(Module(name, "src/$name", useJunitPlatform))
    }

    /**
     * Register a test module with name, directory and use junit test engine, with following defaults (e.g. module name is testInt and test dir is src/my-custom-test-dir)
     * 1. Test engine - Junit platform(Junit 5) if true or else junit4. Defaults to junit5
     * 2. Test dir - src/my-custom-test-dir
     * 3. Test task name - testInt
     * 4. Test configuration - testIntImplementation, testIntRuntimeOnly
     */
    fun module(name: String, dir: String, useJunitPlatform: Boolean = true) {
        modules.add(Module(name, dir, useJunitPlatform))
    }

    data class Module(val name: String, val dir: String, val useJunitPlatform: Boolean)
}
