package ru.nsu.modern.methods.persistent.data.structures.map

import ru.nsu.modern.methods.persistent.data.structures.array.FatNodeArray
import ru.nsu.modern.methods.persistent.data.structures.array.PersistentArray
import ru.nsu.modern.methods.persistent.data.structures.list.FatNodeList
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
        val iterator = presentEntriesIterator()
        return FatNodeList(size) {
            iterator.next()
        }
    }

    override fun toPersistentArray(): PersistentArray<PersistentMap.Entry<K, V>> {
        val iterator = presentEntriesIterator()
        return FatNodeArray(size, version) {
            iterator.next()
        }
    }

    override fun iterator() =
        presentEntriesIterator().asSequence()
            .map { it.value }
            .iterator()

    @Suppress("UNCHECKED_CAST")
    private fun presentEntriesIterator() =
        nodes.values.iterator().asSequence()
            .mapNotNull { it.findValue(version) }
            .filter { it.value != null }
            .map { it as VersionedValue<PersistentMap.Entry<K, V>> }
            .iterator()
}
