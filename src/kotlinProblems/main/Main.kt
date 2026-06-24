package kotlinProblems.main

fun main() {
    val hashMap = HashMap()
    hashMap.add(1, 2)
    hashMap.add(2, 3)
    hashMap.add(3, 4)
    hashMap.add(4, 5)
//    hashMap.display()
}


class HashMap {

    private var size = 0
    private val loadFactor = 0.75
    private var internalArr: Array<Node?> = Array(5) { null }

    fun add(key: Int, value: Int) {
        val hasCode = key.hashCode()
        val idx = (internalArr.size - 1) and hasCode
        putValue(idx, Node(entry = Entry(key = key, value = value)))
        reHashIfNecessary()
        size++
    }

    fun remove(key: Int) {
        val hasCode = key.hashCode()
        val idx = (internalArr.size - 1) and hasCode
        removeValue(idx = idx, key = key)
        size--
    }

    fun containsKey(key: Int): Boolean {
        val hasCode = key.hashCode()
        val idx = hasCode % internalArr.size
        return containsKey(idx, key)
    }

    private fun containsKey(idx: Int, key: Int): Boolean {
        var temp: Node? = internalArr[idx]
        while (temp?.next != null && temp.entry.key != key) {
            temp = temp.next
        }
        return if (temp == null) false else temp.entry.key == key
    }

    private fun putValue(idx: Int, node: Node) {
        if (internalArr[idx] == null || internalArr[idx]?.entry == null) internalArr[idx] = node
        else {
            var temp: Node? = internalArr[idx]
            while (temp?.next != null && temp.entry.key != node.entry.key) {
                temp = temp.next
            }
            if (temp?.entry?.key == node.entry.key) {
                temp.entry.value = node.entry.value
            } else temp?.next = node
        }
    }

    private fun removeValue(idx: Int, key: Int) {
        if (internalArr[idx] == null || internalArr[idx]?.entry == null) return
        if (internalArr[idx]?.entry?.key == key) {
            internalArr[idx] = null
        } else {
            var cur: Node? = internalArr[idx]?.next
            var next = cur?.next
            var prev: Node? = internalArr[idx]
            while (cur != null) {
                if (cur.entry.key == key) {
                    prev?.next = next
                    return
                } else {
                    prev = cur
                    cur = next
                    next = next?.next
                }
            }
        }
    }

    private fun reHashIfNecessary() {
        if (size / (internalArr.size).toDouble() > loadFactor) {
            val oldArr = internalArr
            internalArr = Array(oldArr.size * 2) { null }

            for (node in oldArr) {
                var current = node
                while (current != null) {
                    val hasCode = current.entry.key.hashCode()
                    val idx = (internalArr.size - 1) and hasCode
                    putValue(idx, Node(entry = current.entry))
                    current = current.next
                }
            }

        }
    }

    fun display() {
        println(internalArr.filter { it != null }.joinToString { " $it" })
    }


    data class Node(var next: Node? = null, val entry: Entry) {
        override fun toString(): String {
            return entry.toString()
        }
    }

    data class Entry(val key: Int, var value: Int) {
        override fun toString(): String {
            return "$key -> $value"
        }
    }

}