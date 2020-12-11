package ru.nsu.modern.methods.persistent.data.structures.map

import ru.nsu.modern.methods.persistent.data.structures.PersistentCollection

/**
 * Persistent storage of key-value pairs.
 */
interface PersistentMap<K, V> : PersistentCollection<PersistentMap.Entry<K, V>> {
    /**
     * Holder for key value pair stored in map.
     */
    data class Entry<K, V>(
        val key: K,
        val value: V
    )

    /**
     * Gets value by given [key].
     */
    operator fun get(key: K): V?

    /**
     * Sets value of item with given [key].
     */
    operator fun set(key: K, value: V)

    /**
     * Check if map contains given key.
     */
    fun contains(key: K) : Boolean

    /**
     * Removes given [key]
     */
    fun remove(key: K)
}
