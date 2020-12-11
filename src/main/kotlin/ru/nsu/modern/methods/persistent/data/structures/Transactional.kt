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

    /**
     * If container contains another persistent collection then do redo recursively
     */
    fun redoCascade()

    /**
     * If container contains another persistent collection then do undo recursively
     */
    fun undoCascade()

}
