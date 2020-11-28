package ru.nsu.modern.methods.persistent.data.structures.shared

/**
 * Some value with version.
 */
internal class VersionedValue<T>(
    val value: T,
    val version: Int
)
