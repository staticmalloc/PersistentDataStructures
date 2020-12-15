package ru.nsu.modern.methods.persistent.data.structures.list

import org.junit.Assert
import org.junit.Test
import ru.nsu.modern.methods.persistent.data.structures.assertVersionsIs
import java.lang.IndexOutOfBoundsException

class PersistentListTest {
    private val underTest = FatNodeList<Int>()


    @Test
    fun `add first all versions stored`() {
        //empty list have already had version 0
        underTest.addFirst(0)
        underTest.addFirst(1)
        underTest.addFirst(2)

        Assert.assertEquals(3, underTest.lastVersion)
        Assert.assertEquals(3, underTest.version)

        underTest.version = 2

        Assert.assertEquals(2, underTest.version)

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf(0),
                listOf(1, 0),
                listOf(2, 1, 0)
            )
        )
    }

    @Test
    fun `size test`() {
        Assert.assertEquals(0, underTest.size)
        underTest.addFirst(0)
        Assert.assertEquals(1, underTest.size)
        underTest.addLast(1)
        Assert.assertEquals(2, underTest.size)
        underTest.addFirst(2)
        Assert.assertEquals(3, underTest.size)

        underTest.version = 2
        Assert.assertEquals(2, underTest.size)

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf(0),
                listOf(0, 1),
                listOf(2, 0, 1)
            )
        )
    }

    @Test
    fun `get and addLast test`() {
        underTest.addFirst(0)
        Assert.assertEquals(0, underTest[0])

        underTest.addLast(1)
        underTest.addFirst(2)
        Assert.assertEquals(2, underTest[0])
        Assert.assertEquals(0, underTest[1])
        Assert.assertEquals(1, underTest[2])

        underTest.version = 2
        Assert.assertEquals(2, underTest.size)
    }

    @Test
    fun `set test`() {
        underTest.addFirst(0)
        underTest.addLast(1)
        underTest.addFirst(2)
        underTest[1] = 100
        Assert.assertEquals(100, underTest[1])
        Assert.assertEquals(4, underTest.version)
        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf(0),
                listOf(0, 1),
                listOf(2, 0, 1),
                listOf(2, 100, 1)
            )
        )
    }


    @Test
    fun `add test`() {
        Assert.assertThrows(IndexOutOfBoundsException::class.java) {
            underTest.add(100, 50)
        }

        underTest.addFirst(0)
        underTest.addLast(1)
        underTest.addFirst(2)

        underTest.add(value = 100)
        Assert.assertEquals(100, underTest[3])

        underTest.add(1, 50)
        Assert.assertEquals(50, underTest[1])

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf(0),
                listOf(0, 1),
                listOf(2, 0, 1),
                listOf(2, 0, 1, 100),
                listOf(2, 50, 0, 1, 100)
            )
        )
    }


    @Test
    fun `remove test`() {
        Assert.assertThrows(IndexOutOfBoundsException::class.java) {
            underTest.remove(0)
        }

        underTest.addLast(1)
        underTest.addLast(2)
        underTest.addLast(3)
        underTest.addLast(4)
        underTest.addLast(5)
        underTest.addLast(6)

        Assert.assertEquals(6, underTest.size)

        Assert.assertEquals(6, underTest.remove(5))
        Assert.assertEquals(3, underTest.remove(2))
        Assert.assertEquals(1, underTest.remove(0))

        Assert.assertEquals(3, underTest.size)

        underTest.assertVersionsIs(
            expectedVersions = listOf(
                listOf(),
                listOf(1),
                listOf(1, 2),
                listOf(1, 2, 3),
                listOf(1, 2, 3, 4),
                listOf(1, 2, 3, 4, 5),
                listOf(1, 2, 3, 4, 5, 6),
                listOf(1, 2, 3, 4, 5),
                listOf(1, 2, 4, 5),
                listOf(2, 4, 5)
            )
        )
    }

    @Test
    fun `iterator test`() {
        Assert.assertEquals(false, underTest.iterator().hasNext())
        underTest.addFirst(5)
        underTest.addFirst(10)
        val iterator = underTest.iterator()
        Assert.assertEquals(true, iterator.hasNext())
        Assert.assertEquals(10, iterator.next())
        Assert.assertEquals(true, iterator.hasNext())
        Assert.assertEquals(5, iterator.next())
        Assert.assertEquals(false, iterator.hasNext())
    }
}