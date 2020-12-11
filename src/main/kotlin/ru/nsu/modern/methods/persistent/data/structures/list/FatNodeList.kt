package ru.nsu.modern.methods.persistent.data.structures.list

class FatNodeList<T>() : PersistentList<T> {
    override val size: Int
        get() = TODO("Not yet implemented")
    override val lastVersion: Int
        get() = TODO("Not yet implemented")
    override var version: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }

    override fun insert(index: Int, value: T) {
        TODO("Not yet implemented")
    }

    override fun get(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, value: T) {
        TODO("Not yet implemented")
    }

    override fun remove(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun toPersistentList() {
        TODO("Not yet implemented")
    }

    override fun toPersistentArray() {
        TODO("Not yet implemented")
    }
}