package ru.nsu.modern.methods.persistent.data.structures

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
}
