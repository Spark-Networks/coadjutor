package de.affinitas.coadjutor

import de.affinitas.PropertiesHandler
import java.io.File
import java.util.*

class OrderedProperties(private val path: File) {
    private val properties: LinkedHashMap<String, String?> = LinkedHashMap()

    companion object {
        fun load(file: File): OrderedProperties {
            val orderedProps = OrderedProperties(file)
            PropertiesHandler(orderedProps.properties).load(file.inputStream())
            return orderedProps
        }
    }

    fun get(key: String): String? {
        return properties[key]
    }

    fun getAsOptional(key: String): Optional<String> {
        return Optional.ofNullable(properties[key])
    }

    fun get(key: String, defaultValue: String): String {
        val value = properties[key]
        return value ?: defaultValue
    }

    fun set(key: String, value: String): OrderedProperties {
        properties[key] = value
        return this
    }

    fun delete(key: String): String? {
        return properties.remove(key)
    }

    fun has(key: String): Boolean {
        return properties.containsKey(key)
    }

    fun size(): Int {
        return properties.size
    }

    fun isEmpty(): Boolean {
        return properties.isEmpty()
    }

    fun getEntry(key: String): Entry {
        return Entry(key, properties[key])
    }

    fun save(comment: String) {
        PropertiesHandler(properties).store(path.outputStream(), comment)
    }

    data class Entry(val key: String, val value: String?) {
        val isEmpty = value.isNullOrBlank()
    }
}
