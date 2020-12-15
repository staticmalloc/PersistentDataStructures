package ru.nsu.modern.methods.persistent.data.structures.shared

internal class VersionedReference<T>(
    override val version: Int,
    val reference: FatNode<VersionedValue<T>>? = null
) : Versioned<T>