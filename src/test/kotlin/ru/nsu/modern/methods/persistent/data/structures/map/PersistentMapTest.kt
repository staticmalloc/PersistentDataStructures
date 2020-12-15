package ru.nsu.modern.methods.persistent.data.structures.map

import org.junit.Assert
import org.junit.Test
import ru.nsu.modern.methods.persistent.data.structures.assertVersionsIs

class PersistentMapTest {
    private val underTest = FatNodeMap<String, Int>()

    @Test
    fun `all previous versions is stored`() {
        underTest["Key1"] = 1
        underTest["Key2"] = 2
        underTest.remove("Key1")
        underTest["Key2"] = 5
        underTest.remove("Key2")

        underTest.assertVersionsIs(
            listOf(
                listOf(),
                listOf("Key1" to 1),
                listOf("Key1" to 1, "Key2" to 2),
                listOf("Key2" to 2),
                listOf("Key2" to 5),
                listOf()
            )
        )
    }

    @Test
    fun `previous version doesn't contain added key`() {
        underTest["key"] = 2
        Assert.assertEquals(2, underTest["key"])
        underTest.version = 0
        Assert.assertFalse(underTest.contains("key"))
    }

    @Test
    fun `previous version contains removed key`() {
        underTest["key"] = 2
        underTest.remove("key")
        Assert.assertFalse(underTest.contains("key"))
        underTest.version = 1
        Assert.assertTrue(underTest.contains("key"))
    }

    @Test
    fun `toPersistentArray returns array with map entries and same version`() {
        underTest["Key1"] = 1
        underTest["Key2"] = 2

        val persistentArray = underTest.toPersistentArray()
        Assert.assertEquals(underTest.version, persistentArray.version)
        Assert.assertEquals(underTest.toList(), persistentArray.toList())
    }

    @Test
    fun `when array returned by toPersistentArray is modified source map is not changed`() {
        underTest["Key1"] = 1
        underTest["Key2"] = 2

        val persistentArray = underTest.toPersistentArray()
        persistentArray[0] = "a" to 1
        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf("Key1" to 1),
                listOf("Key1" to 1, "Key2" to 2)
            )
        )
    }

    private infix fun <K, V> K.to(value: V) = PersistentMap.Entry(this, value)
}
