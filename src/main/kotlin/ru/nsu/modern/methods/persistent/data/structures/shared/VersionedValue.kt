package ru.nsu.modern.methods.persistent.data.structures.shared

/**
 * Some value with version.
 */

internal class VersionedValue<T>(
    val value: T,
    val version: Int,
    val next: FatNode<T>? = null,
    val prev: FatNode<T>? = null
)
