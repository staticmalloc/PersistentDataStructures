package ru.nsu.modern.methods.persistent.data.structures

import org.junit.Assert

fun <T> PersistentCollection<T>.assertVersionsIs(expectedVersions: List<List<T>>) {
    Assert.assertEquals(expectedVersions.size, lastVersion + 1)
    repeat(expectedVersions.size) {
        version = it
        Assert.assertEquals(expectedVersions[it], toList())
    }
}
