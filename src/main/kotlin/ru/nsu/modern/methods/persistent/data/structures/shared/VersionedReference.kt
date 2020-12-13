package ru.nsu.modern.methods.persistent.data.structures.shared

internal class VersionedReference<T>(
    val version: Int,
    val reference: FatNode<T>? = null
)