package kotlinProblems

fun main() {
    doSomething {
        println("Doing something...")
    }
}

inline fun doSomething(action: () -> Unit) {
    println("Start")
    action()
    println("End")
}
