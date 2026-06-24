package kotlinProblems.implementation.linkedList

class LinkedList {


    var head: Node? = null

    fun remove() {

    }

    fun addLast(data: Int): Node {
        val newNode = Node(null, data)
        if (head == null) {
            head = newNode
        } else {
            var temp: Node? = head
            while (temp?.next != null) {
                temp = temp.next
            }
            temp?.next = newNode
        }
        return newNode
    }

    fun addFirst(data: Int): Node {
        val newNode = Node(head, data)
        head = newNode
        return newNode
    }

    fun insertAt(pos: Int, data: Int) {
        
    }


    class Node(var next: Node?, val data: Int) {
        constructor() : this(null, 0)
    }


}