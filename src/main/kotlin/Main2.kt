fun main() {

    val outerClass1 = OuterClass1("Jimbo")
    println(outerClass1.name)
    outerClass1.setLazyAndLateinit()

    val outerClass1Nested = OuterClass1.Nested() // note only last is instantiated
    outerClass1Nested.printMessage("Hello there")

    val outerClass1MoreNested = OuterClass1.Nested.MoreNested() // note only last is instantiated
    outerClass1MoreNested.printAnother("Hello there2")

    val outerClass2Nested = OuterClass2().Nested() // note both are instantiated
    outerClass2Nested.printMessage()

    val outerClass3Nested = OuterClass2().Nested().MoreNested() // note all are instantiated
    outerClass3Nested.printAnother()

    Numbers.helloMate()
    println(Numbers.APP_NAME)

    var x = OuterClass1.randomNumber()
    OuterClass1.x = x
    OuterClass1.randomNumber()

}

class OuterClass1(name: String) {
    val apple = "Apple"
    val number = 250
    lateinit var a: String
    val b: String by lazy {
        randomNumber().toString()
    }

    private val _name: String = name
    val name: String
        get() = _name

    val nested = Nested() // ok
    val moreNested = Nested.MoreNested() // ok

    class Nested {
        fun printMessage(message: String) { // no access to outer scopes
            println(message + randomNumber())
        }

        class MoreNested { // no access to outer scopes
            fun printAnother(message: String) = println(message)
        }
    }

    fun setLazyAndLateinit() {
        a = randomNumber().toString()
        println("lateinit= $a, lazy=$b")
    }

    companion object { // These are like Java static functions and vars
        var x = 0
        var y = 100

        fun randomNumber(): Int {
            val random = (x..y).random()
            println("Random number = $random")
            return random
        }
    }
}

class OuterClass2 {
    val apple = "Apple"
    val number = 250

    fun printAnimal() {
        println(Nested().animal)
    }

    inner class Nested {  // note: use of inner allows access of outer class scopes
        val animal = "cat"
        fun printMessage() {
            println("$apple is $number")
        }

        inner class MoreNested { // note: use of inner allows access of all outer class scopes
            fun printAnother() {
                println("$number is $apple likes $animal")
            }
        }
    }
}