package ru.nsu.modern.methods.persistent.data.structures.array

import ru.nsu.modern.methods.persistent.data.structures.PersistentCollection

/**
 * Persistent data structure with fixed number of elements.
 */
interface PersistentArray<T>: PersistentCollection<T> {
    /**
     * Gets value of item with [index].
     */
    operator fun get(index: Int): T

    /**
     * Sets value of item with [index].
     */
    operator fun set(index: Int, value: T)
}
