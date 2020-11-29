package ru.nsu.modern.methods.persistent.data.structures.shared


/**
 * Contains list of versioned values.
 * Allows to find value by version.
 */
internal class FatNode<T>(
    initialValue: VersionedValue<T>
) {
    private val values = mutableListOf(initialValue)

    private val lastVersion get() = values.last().version

    /**
     * Adds [versionedValue] to fat node.
     */
    fun addValue(versionedValue: VersionedValue<T>) {
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
     */
    fun findValue(version: Int): VersionedValue<T> {
        //TODO: Faster implementation (now it is O(n))
        return values.last { it.version <= version }
    }
}
