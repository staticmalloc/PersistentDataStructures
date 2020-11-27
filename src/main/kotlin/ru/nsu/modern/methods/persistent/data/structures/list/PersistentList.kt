package ru.nsu.modern.methods.persistent.data.structures.list

import ru.nsu.modern.methods.persistent.data.structures.PersistentCollection

/**
 * Persistent double-direction linked list.
 */
interface PersistentList<T>: PersistentCollection<T> {
    /**
     * Inserts [value] after given [index].
     */
    fun insert(index: Int, value: T)

    /**
     * Gets value of item with [index].
     */
    operator fun get(index: Int): T

    /**
     * Sets value of item with [index] to [value].
     */
    operator fun set(index: Int, value: T)

    /**
     * Removes item by [index].
     */
    fun remove(index: Int): T
}
