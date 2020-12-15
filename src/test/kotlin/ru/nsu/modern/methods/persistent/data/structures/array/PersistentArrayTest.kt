package ru.nsu.modern.methods.persistent.data.structures.array

import org.junit.Assert
import org.junit.Test
import ru.nsu.modern.methods.persistent.data.structures.assertVersionsIs

class PersistentArrayTest {
    private val underTest = FatNodeArray(3) { 0 }

    @Test
    fun `all previous versions are stored`() {
        underTest[0] = 1
        underTest[2] = 2
        underTest[2] = 3
        underTest[1] = 4
        underTest[1] = 5

        Assert.assertEquals(5, underTest.lastVersion)
        Assert.assertEquals(5, underTest.version)

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(0, 0, 0),
                listOf(1, 0, 0),
                listOf(1, 0, 2),
                listOf(1, 0, 3),
                listOf(1, 4, 3),
                listOf(1, 5, 3)
            )
        )

    }

    @Test
    fun `all next versions are removed if current version is not last and array is modified`() {
        underTest[0] = 10
        underTest[1] = 5
        underTest[2] = 3
        underTest.version = 1
        underTest[2] = 4

        Assert.assertEquals(2, underTest.lastVersion)
        Assert.assertEquals(2, underTest.version)

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(0, 0, 0),
                listOf(10, 0, 0),
                listOf(10, 0, 4)
            )
        )
    }

    @Test
    fun `undo returns to previous version`() {
        underTest[0] = 5
        underTest[1] = 6
        underTest.undo()

        Assert.assertEquals(listOf(5, 0, 0), underTest.toList())
    }

    @Test
    fun `redo after undo returns to current version`() {
        underTest[0] = 5
        underTest[1] = 6
        underTest.undo()
        underTest.redo()

        Assert.assertEquals(listOf(5, 6, 0), underTest.toList())
    }

    @Test
    fun `toPersistentArray returns same array`() {
        Assert.assertSame(underTest, underTest.toPersistentArray())
    }
}
