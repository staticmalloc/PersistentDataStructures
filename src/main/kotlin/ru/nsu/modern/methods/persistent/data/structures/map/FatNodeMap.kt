package ru.nsu.modern.methods.persistent.data.structures.map

import ru.nsu.modern.methods.persistent.data.structures.array.PersistentArray
import ru.nsu.modern.methods.persistent.data.structures.list.PersistentList
import ru.nsu.modern.methods.persistent.data.structures.shared.FatNode
import ru.nsu.modern.methods.persistent.data.structures.shared.VersionedValue

class FatNodeMap<K, V> : PersistentMap<K, V> {
    private val nodes = mutableMapOf<K, FatNode<VersionedValue<PersistentMap.Entry<K, V>?>>>()

    override val size: Int
        get() = nodes.size

    override var lastVersion = 0
        private set

    override var version = 0

    override fun get(key: K): V? {
        return nodes[key]?.findValue(version)?.value?.value
    }

    override fun set(key: K, value: V) {
        makeNewVersion()
        val versionedValue = VersionedValue<PersistentMap.Entry<K, V>?>(PersistentMap.Entry(key, value), version)
        val node = nodes[key]
        if (node == null) {
            nodes[key] = FatNode(initialValue = versionedValue)
            return
        }
        node.addValue(versionedValue)
    }

    override fun contains(key: K): Boolean {
        return this[key] != null
    }

    override fun remove(key: K) {
        makeNewVersion()
        val node = nodes[key] ?: return
        node.addValue(VersionedValue(null, version))
    }

    private fun makeNewVersion() {
        val newVersion = version + 1
        if (newVersion <= lastVersion) {
            nodes.forEach { it.value.removeVersionsFrom(newVersion) }
        }
        lastVersion = newVersion
        version = newVersion
    }

    override fun toPersistentList(): PersistentList<PersistentMap.Entry<K, V>> {
        TODO()
    }

    override fun toPersistentArray(): PersistentArray<PersistentMap.Entry<K, V>> {
        TODO("not implemented")
    }

    override fun iterator() = object : Iterator<PersistentMap.Entry<K, V>> {
        private val nodesIterator = nodes.iterator()
        private var next: PersistentMap.Entry<K, V>? = null

        override fun hasNext(): Boolean {
            if (next != null) {
                return true
            }
            while (nodesIterator.hasNext()) {
                val node = nodesIterator.next()
                val value = node.value.findValue(version)?.value
                if (value != null) {
                    next = value
                    return true
                }
            }
            return false
        }

        override fun next(): PersistentMap.Entry<K, V> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val nextOld = next!!
            next = null
            return nextOld
        }
    }
}
