package ru.nsu.modern.methods.persistent.data.structures.shared


/**
 * Contains list of versioned values.
 * Allows to find value by version.
 */
internal class FatNode<T : Versioned<*>>(
    initialValue: T
) {
    private val values = mutableListOf(initialValue)

    private val lastVersion get() = values.last().version

    /**
     * Adds [versionedValue] to fat node.
     */
    fun addValue(versionedValue: T) {
        if (versionedValue.version <= lastVersion) {
            removeVersionsFrom(versionedValue.version)
        }
        values.add(versionedValue)
    }

    /**
     * Removes versions >= [version].
     */
    fun removeVersionsFrom(version: Int) {
        values.removeIf { it.version >= version }
    }

    /**
     * Finds value with max version <= [version].
     * Return null if version is not found.
     */
    fun findValue(version: Int): T? {
        val binarySearchResult = values.binarySearch { it.version - version }
        val index = if (binarySearchResult < 0) {
            -binarySearchResult - 2
        } else {
            binarySearchResult
        }
        if (index < 0) {
            return null
        }
        return values[index]
    }


    fun isEmpty() = values.isEmpty()
}
