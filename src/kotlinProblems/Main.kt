package kotlinProblems

fun main() {

    var a = 22
    var b = 3

    var ans = 0

    while (a >= b) {

        var counter = 0

        while(b shl counter <= a){
            counter++
        }

        counter--

        ans += 1 shl counter
        a -= b shl counter

    }

    println(ans)


}
