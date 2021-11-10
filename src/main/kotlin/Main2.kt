fun main() {
    val outerClass1Nested = OuterClass1.Nested()
    outerClass1Nested.printMessage("Hello there")

    val outerClass1MoreNested = OuterClass1.Nested.MoreNested()
    outerClass1MoreNested.printAnother("Hello there2")

    val outerClass2Nested = OuterClass2().Nested() // note both are instantiated
    outerClass2Nested.printMessage()

    val outerClass3Nested = OuterClass2().Nested().MoreNested() // note all are instantiated
    outerClass3Nested.printAnother()
}

class OuterClass1 {
    val apple = "Apple"
    val number = 250

    class Nested {
        fun printMessage(message: String) { // no access to outer scopes
            println(message)
        }

        class MoreNested { // no access to outer scopes
            fun printAnother(message: String) = println(message)
        }
    }
}

class OuterClass2 {
    val apple = "Apple"
    val number = 250

    inner class Nested {  // note: use of inner allows access of outer class scopes
        val animal = "cat"
        fun printMessage() {
            println("$apple is $number")
        }

        inner class MoreNested { // note: use of inner allows access of outer class scopes
            fun printAnother() {
                println("$number is $apple likes $animal")
            }
        }
    }
}