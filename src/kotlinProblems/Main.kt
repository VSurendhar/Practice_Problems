package kotlinProblems

sealed class Shape {
    abstract fun area(): Double
    abstract fun perimeter(): Double
}

class Circle : Shape() {
    override fun area(): Double {
        return 0.0
    }

    override fun perimeter(): Double {
        return 0.0
    }
}

class Square : Shape() {
    override fun area(): Double {
        return 0.0
    }

    override fun perimeter(): Double {
        return 0.0
    }
}

fun main() {
    val circle: Circle = Circle()
    println(circle.area())
}