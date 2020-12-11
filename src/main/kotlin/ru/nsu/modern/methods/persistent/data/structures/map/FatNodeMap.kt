package main.kotlin.ru.nsu.modern.methods.persistent.data.structures.map

import ru.nsu.modern.methods.persistent.data.structures.map.PersistentMap

class FatNodeMap<K, V>() : PersistentMap<K, V> {
    override fun get(key: K): V {
        TODO("Not yet implemented")
    }

    override fun set(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K) {
        TODO("Not yet implemented")
    }

    override val size: Int
        get() = TODO("Not yet implemented")
    override val lastVersion: Int
        get() = TODO("Not yet implemented")
    override var version: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun iterator(): Iterator<PersistentMap.Entry<K, V>> {
        TODO("Not yet implemented")
    }

    override fun toPersistentList() {
        TODO("Not yet implemented")
    }

    override fun toPersistentArray() {
        TODO("Not yet implemented")
    }
}