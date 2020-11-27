package ru.nsu.modern.methods.persistent.data.structures

/**
 * Describes entity with ability to undo and redo last state modifications.
 */
interface Transactional {
    /**
     * Undo last modification.
     */
    fun undo()

    /**
     * Redo modification.
     */
    fun redo()
}
