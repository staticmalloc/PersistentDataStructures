package ru.nsu.modern.methods.persistent.data.structures.shared

/**
 * Some value with version.
 */

internal class VersionedValue<T>(
    val value: T,
    override val version: Int,
    var next: FatNode<VersionedValue<T>>? = null,
    var prev: FatNode<VersionedValue<T>>? = null
) : Versioned<T>
