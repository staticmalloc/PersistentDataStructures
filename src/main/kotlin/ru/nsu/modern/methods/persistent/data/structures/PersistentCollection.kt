package ru.nsu.modern.methods.persistent.data.structures

import ru.nsu.modern.methods.persistent.data.structures.array.PersistentArray
import ru.nsu.modern.methods.persistent.data.structures.list.PersistentList

/**
 * Base interface for all persistent data structures.
 * Provides access to previous versions of data structure.
 */
interface PersistentCollection<T> : Iterable<T>, Transactional {
    /**
     * Number of items in collection
     */
    val size: Int

    /**
     * Last version number.
     */
    val lastVersion: Int

    /**
     * Current version number.
     * Any version can be accessed by modifying this number.
     * Should be from 0 to [lastVersion]
     */
    var version: Int

    /**
     * Default undo implementation.
     * Decreases version number by one.
     */
    override fun undo() {
        if (version > 0) {
            --version
        }
    }

    /**
     * Default redo implementation.
     * Increases version number by one.
     */
    override fun redo() {
        if (version < lastVersion) {
            ++version
        }
    }

    override fun undoCascade() {
        undo()
        forEach {
            if (it is PersistentCollection<*>) {
                it.undoCascade()
            }
        }
    }

    override fun redoCascade() {
        redo()
        forEach {
            if (it is PersistentCollection<*>) {
                it.redoCascade()
            }
        }
    }

    /**
     * Memory effectively converts collection to persistent list.
     */
    fun toPersistentList(): PersistentList<T>

    /**
     * Memory effectively converts collection to persistent array.
     */
    fun toPersistentArray(): PersistentArray<T>
}
