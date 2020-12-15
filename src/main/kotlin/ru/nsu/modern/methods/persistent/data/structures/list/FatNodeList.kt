package ru.nsu.modern.methods.persistent.data.structures.list

import ru.nsu.modern.methods.persistent.data.structures.array.FatNodeArray
import ru.nsu.modern.methods.persistent.data.structures.array.PersistentArray
import ru.nsu.modern.methods.persistent.data.structures.shared.FatNode
import ru.nsu.modern.methods.persistent.data.structures.shared.VersionedReference
import ru.nsu.modern.methods.persistent.data.structures.shared.VersionedValue
import java.lang.IndexOutOfBoundsException

/**
 * Fat node implementation of persistent List.
 */
class FatNodeList<T>() : PersistentList<T> {
    private val head = FatNode(VersionedReference<T>(0)) //references for each version list head
    private val allNodes = mutableListOf<FatNode<VersionedValue<T>>>() //references for exclude memory leak
    private var minVersion = 0


    /**
     * Constructor for toPersistentList() methods
     * It re'uses references from other's collections
     */

    internal constructor(
        size: Int,
        init: (index: Int) -> VersionedValue<T>
    ) : this() {
        var maxVersion = 0
        head.removeVersionsFrom(0)
        var prevVersionedValue = init(0)
        prevVersionedValue.prev = null
        maxVersion = kotlin.math.max(maxVersion, prevVersionedValue.version)
        var prevNode = FatNode(prevVersionedValue)
        head.addValue(VersionedReference(0, prevNode)) //add reference to first node to head
        allNodes.add(prevNode) //add first node
        for (i in 1..size - 1) {
            val curVersionedValue = init(i)
            maxVersion = kotlin.math.max(maxVersion, curVersionedValue.version)
            val node = FatNode(curVersionedValue)
            prevVersionedValue.next = node
            curVersionedValue.prev = prevNode
            allNodes.add(node)
            prevNode = node
            prevVersionedValue = curVersionedValue
        }
        prevVersionedValue.next = null
        minVersion = maxVersion
        version = maxVersion
        lastVersion = maxVersion
    }


    /**
     * Increment version
     * Remove higher versions for undo/redo mechanism
     */
    private fun makeNewVersion() {
        val newVersion = version + 1
        if (newVersion <= lastVersion) {
            head.removeVersionsFrom(newVersion)
            allNodes.forEach { it.removeVersionsFrom(newVersion) }
            allNodes.removeIf { it.isEmpty() }
        }
        lastVersion = newVersion
        version = newVersion
    }

    /**
     * Finds last fat node depends on version
     */
    private fun findLastFatNode(ver: Int): FatNode<VersionedValue<T>>? {
        val versionedReference = head.findValue(ver)
        var lastFatNode = versionedReference!!.reference  // find last list element with prev version
        var next = lastFatNode!!.findValue(ver)!!.next
        while (next != null) {
            lastFatNode = next
            next = lastFatNode.findValue(ver)!!.next
        }
        return lastFatNode
    }

    /**
     * Finds fat node at position [index] depends on version
     */
    private fun fatNodeAt(index: Int, ver: Int): FatNode<VersionedValue<T>>? {
        val versionedReference = head.findValue(version)
        var fatNode = versionedReference!!.reference  // find last list element with prev version
        var s = 0
        var next = fatNode!!.findValue(ver)!!.next
        while (next != null && s < index) {
            fatNode = next
            next = fatNode.findValue(ver)!!.next
            s++
        }
        return fatNode
    }

    /**
     * Returns size of list depends on current [version]
     */
    override var size: Int = 0
        private set
        get() {
            val versionedReference = head.findValue(version)
            var lastFatNode = versionedReference!!.reference  // find last list element with prev version
            if (lastFatNode != null) {
                var s = 1
                var next = lastFatNode.findValue(version)!!.next
                while (next != null) {
                    lastFatNode = next
                    next = lastFatNode.findValue(version)!!.next
                    s++
                }
                return s
            }
            return 0
        }

    override var lastVersion: Int = 0
        get() = field - minVersion
        private set


    override var version: Int = 0
        get() = field - minVersion
        set(value) {
            require(value in 0..lastVersion) {
                "Incorrect version number: $value, Last version is: $lastVersion"
            }
            field = value
        }


    /**
     * Inserts the specified element at the specified position in this list.
     */
    override fun add(index: Int, value: T) {
        when {
            index == 0 -> addFirst(value)
            (index == size) or (index == -1) -> addLast(value)
            index in 1..size - 1 -> {
                makeNewVersion()
                val versionedValue = VersionedValue(value, version)
                val newFatNode = FatNode(versionedValue)
                val curFatNode = fatNodeAt(index, version - 1)
                val lastCurVersionedValue = curFatNode?.findValue(version - 1) ?: throw NoSuchElementException()
                curFatNode.addValue(
                    VersionedValue(
                        lastCurVersionedValue.value,
                        version,
                        lastCurVersionedValue.next,
                        newFatNode
                    )
                )
                val prevFatNode = lastCurVersionedValue.prev
                val lastPrevVersionedValue = prevFatNode!!.findValue(version - 1)
                prevFatNode.addValue(
                    VersionedValue(
                        lastPrevVersionedValue!!.value,
                        version,
                        newFatNode,
                        lastPrevVersionedValue.prev
                    )
                )
                versionedValue.next = curFatNode
                versionedValue.prev = prevFatNode
                allNodes.add(newFatNode)
            }
            else -> throw IndexOutOfBoundsException()
        }
    }


    /**
     * Inserts the specified element at the beginning of this list
     */
    override fun addFirst(element: T) {
        makeNewVersion()
        val versionedReference = head.findValue(version - 1)
        val nextRef = if (versionedReference != null) versionedReference.reference else null
        val fatn = FatNode(VersionedValue(element, version, nextRef)) //create new element
        if (nextRef != null) {
            val prevVersionedValue = nextRef.findValue(version - 1) // find next el in fatnode
            // create copy with newer version and previous reference to the created element <fatn>
            nextRef.addValue(VersionedValue(prevVersionedValue!!.value, version, prevVersionedValue.next, fatn))
        }
        allNodes.add(fatn) // put new element to list of all fatnodes
        head.addValue(VersionedReference(version, fatn))  //add new refence to head list
    }


    /**
     * Inserts the specified element at the end of this list
     */
    override fun addLast(element: T) {
        val ref = head.findValue(version)
        if (ref!!.reference == null) {
            addFirst(element)
            return
        }
        makeNewVersion()
        val lastFatNode = findLastFatNode(version - 1)
        val fatn = FatNode(VersionedValue(element, version, null, lastFatNode)) //create new element
        val prevVersionedValue = lastFatNode!!.findValue(version - 1)
        lastFatNode.addValue(VersionedValue(prevVersionedValue!!.value, version, fatn, prevVersionedValue.prev))
        allNodes.add(fatn)
    }

    override fun iterator() = object : ListIterator<T> {
        val vr = head.findValue(version)
        var curNode = if (vr == null) null else vr.reference
        var index = 0


        override fun hasNext(): Boolean {
            if(curNode == null)
                return false
            if(index == 0)
                return true
            return curNode != null
        }

        override fun hasPrevious(): Boolean {
            if(curNode == null)
                return false
            val vv = curNode!!.findValue(version)
            return vv!!.prev != null
        }

        override fun previousIndex() = if (index <= 0) 0 else index - 1

        override fun nextIndex() = if (index <= 0) 0 else index + 1

        override fun previous(): T {
            val vv = curNode!!.findValue(version)
            curNode = vv!!.prev
            index--
            return vv.value
        }

        override fun next(): T {
            val vv = curNode!!.findValue(version)
            curNode = vv!!.next
            index++
            return vv.value
        }

    }


    override fun get(index: Int): T {
        val fatNode = fatNodeAt(index, version)
        return fatNode!!.findValue(version)?.value ?: throw NoSuchElementException()
    }

    override fun set(index: Int, value: T) {
        makeNewVersion()
        val fatNode = fatNodeAt(index, version - 1)
        val lastVersion = fatNode?.findValue(version - 1) ?: throw NoSuchElementException()
        val versionedValue = VersionedValue(value, version, lastVersion.next, lastVersion.prev)
        fatNode.addValue(versionedValue)
    }

    override fun remove(index: Int): T {
        when {
            size == 0 -> throw IndexOutOfBoundsException()
            index == 0 -> {
                makeNewVersion()
                val fatNodeAt0 = fatNodeAt(0, version - 1)
                val lastVersionedValue = fatNodeAt0!!.findValue(version - 1)
                head.addValue(VersionedReference(version, lastVersionedValue!!.next))
                return lastVersionedValue.value
            }
            index == size - 1 -> {
                makeNewVersion()
                val fatNodePreLast = fatNodeAt(index - 1, version - 1)
                val versionedValue = fatNodePreLast!!.findValue(version - 1)
                fatNodePreLast.addValue(VersionedValue(versionedValue!!.value, version, null, versionedValue.prev))
                return fatNodeAt(index, version - 1)!!.findValue(version - 1)!!.value
            }
            index in 1..size - 1 -> {
                makeNewVersion()
                val curFatNode = fatNodeAt(index, version - 1)
                val lastCurVersionedValue = curFatNode?.findValue(version - 1) ?: throw NoSuchElementException()
                val prevFatNode = lastCurVersionedValue.prev
                val nextFatNode = lastCurVersionedValue.next
                val lastPrevVersionedValue = prevFatNode!!.findValue(version - 1)
                val nextNextVersionedValue = nextFatNode!!.findValue(version - 1)
                nextFatNode.addValue(
                    VersionedValue(
                        nextNextVersionedValue!!.value,
                        version,
                        nextNextVersionedValue.next,
                        prevFatNode
                    )
                )
                prevFatNode.addValue(
                    VersionedValue(
                        lastPrevVersionedValue!!.value,
                        version,
                        nextFatNode,
                        lastPrevVersionedValue.prev
                    )
                )
                return lastCurVersionedValue.value
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun toPersistentList(): PersistentList<T> {
        return this
    }

    override fun toPersistentArray(): PersistentArray<T> {
        val head = head.findValue(version) ?: throw IllegalStateException("Head is not defined for version: $version")
        var currentValue = head.reference?.findValue(version)
        return FatNodeArray(size, version) {
            val value = currentValue!!
            currentValue = value.next?.findValue(version)
            value
        }
    }
}
