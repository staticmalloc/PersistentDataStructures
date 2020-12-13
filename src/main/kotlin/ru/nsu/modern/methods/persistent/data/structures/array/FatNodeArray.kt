package ru.nsu.modern.methods.persistent.data.structures.array

import ru.nsu.modern.methods.persistent.data.structures.list.PersistentList
import ru.nsu.modern.methods.persistent.data.structures.shared.FatNode
import ru.nsu.modern.methods.persistent.data.structures.shared.VersionedValue

/**
 * Fat node implementation of persistent array.
 */
class FatNodeArray<T>(
    size: Int,
    init: (index: Int) -> T
) : PersistentArray<T> {

    private val nodes = Array(
        size
    ) { index ->
        FatNode(
            initialValue = VersionedValue(
                value = init(index),
                version = 0
            )
        )
    }

    override val size: Int
        get() = nodes.size

    override var lastVersion = 0
        private set

    override var version = 0
        set(value) {
            require(value in 0..lastVersion) {
                "Incorrect version number: $value, Last version is: $lastVersion"
            }
            field = value
        }

    override fun get(index: Int): T {
        return nodes[index].findValue(version)?.value ?: throw NoSuchElementException()
    }

    override fun set(index: Int, value: T) {
        makeNewVersion()
        val versionedValue = VersionedValue(value, version)
        nodes[index].addValue(versionedValue)
    }

    private fun makeNewVersion() {
        val newVersion = version + 1
        if (newVersion <= lastVersion) {
            nodes.forEach { it.removeVersionsFrom(newVersion) }
        }
        lastVersion = newVersion
        version = newVersion
    }

    override fun iterator() = object : Iterator<T> {
        var index = 0

        override fun hasNext() = index < size

        override fun next() = this@FatNodeArray[index++]
    }

    override fun toPersistentList(): PersistentList<T> {
        TODO("Not yet implemented")
    }

    override fun toPersistentArray(): PersistentArray<T> {
        return this
    }
}
