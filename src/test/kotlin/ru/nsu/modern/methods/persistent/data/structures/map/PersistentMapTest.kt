package ru.nsu.modern.methods.persistent.data.structures.map

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

    private infix fun <K, V> K.to(value: V) = PersistentMap.Entry(this, value)
}
