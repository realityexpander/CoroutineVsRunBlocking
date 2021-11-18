fun main() {

    val enum1 = Enum1.ALPHA1
    println("enum1 = $enum1")
    println("enum1.name = ${enum1.name}")
    println("enum1.named = ${enum1.named}")
    println("enum1.ordinal = ${enum1.ordinal}")
    println("enum1.valueOf = ${Enum1.valueOf("ALPHA1")}")
    println("enum1.values()[1] = ${Enum1.values()[1]}")
    println()

    val enum2 = Enum2.BETA2
    println("enum2 = $enum2")
    println("enum2.name = ${enum2.name}")
    println("enum2.ordinal = ${enum2.ordinal}")
    println("enum2.valueOf = ${Enum2.valueOf("ALPHA2")}")
    println("enum2.named = ${enum2.named}")
    println("enum2.order = ${enum2.order}")
    println("Enum2.valueOf = ${Enum2.valueOf("ALPHA2")}")
    println("Enum2.values()[1] = ${Enum2.values()[1]}")
    println("Enum2.values() = ${Enum2.values().toList()}")
    println()

    val sealed1Alpha = Sealed1.ALPHA3
    println("sealed1Alpha = $sealed1Alpha")  // Just prints type and address ie: Sealed1$ALPHA3@b4c966a
    println("sealed1Alpha.named = ${sealed1Alpha.named}")
    val sealed1AlphaUnNamed = Sealed1.ALPHA3UnNamed
    println("sealed1AlphaUnNamed = $sealed1AlphaUnNamed")  // Just prints type and address ie: Sealed1$ALPHA3@b4c966a
//    println("Sealed1.valueOf = ${Sealed1.valueOf("ALPHA2")}") // cant get the valueOf unlike Enums
//    println("Sealed1.values() = ${Sealed1.values().toList()}") // cant get the values()
    val sealed1Beta = Sealed1.BETA3("Beta")
    println("sealed1Beta = $sealed1Beta")  // Just prints type and address ie: Sealed1$BETA3@b4c966a
    println("sealed1Beta.named = ${sealed1Beta.named}")
    val sealed1Gamma = Sealed1.GAMMA3("Gamma")
    println("sealed1Gamma = ${sealed1Gamma.named}")
    val sealed1Delta = Sealed1.DELTA3("Delta", "Variant")
    println("sealed1Delta = $sealed1Delta")
    println()

    val sealed2Alpha = Sealed2.ALPHA4
    println("sealed2Alpha = ${sealed2Alpha.name} ")
    val sealed2Beta = Sealed2.BETA4("Beta")
    println("sealed2Beta = ${sealed2Beta.name} ")
    val sealed2Gamma = Sealed2.GAMMA4("Gamma")
    println("sealed2Gamma = ${sealed2Gamma.name2}")
    println()


    val sealed3Beta = Sealed3.BETA(MyCustomType("Beta", 10))
    println("sealed3Beta = ${sealed3Beta.obj} ")
    val sealed3Gamma = Sealed3.GAMMA(MyCustomType2("Lisbon", 20.2f))
    println("sealed3Gamma.obj = ${sealed3Gamma.obj}")
    println("sealed3Gamma.obj2 = ${sealed3Gamma.obj2}")
    val sealed3Delta = Sealed3.DELTA(
        MyCustomType("Delta", 10),
        MyCustomType2("NewYork", 23.4f)
    )
    println("sealed3Delta.obj = ${sealed3Delta.obj}")
    println("sealed3Delta.obj2 = ${sealed3Delta.obj2}")


}


enum class Enum1 {
    ALPHA1("AlphaOne"),
    BETA1("BetaOne"),
    GAMMA1("GammaOne");

    constructor(named: String) {
        this.named = named
    }

    val named:String
}

enum class Enum2(val named: String, val order: Int) {
    ALPHA2("AlphaTwo", 100),
    BETA2("BetaTwo", 200),
    GAMMA2("GammaTwo", 300)
}

sealed class Sealed1() {
    object ALPHA3UnNamed: Sealed1() // named param is not required, but if accessed will cause an exception
    object ALPHA3: Sealed1("AlphaName")
    class BETA3(name1: String): Sealed1(name1)
    data class GAMMA3(val name: String): Sealed1(name)
    data class DELTA3(val name2: String, var name3: String): Sealed1(name2)

    constructor(named: String) : this() {
        this.named = named
    }

    lateinit var named: String
}

sealed class Sealed2(val name: String="Default") {
    object ALPHA4: Sealed2()
    class BETA4(name: String): Sealed2(name)
    data class GAMMA4(var name2: String): Sealed1("Gamma default")
}

data class MyCustomType(val name: String, val value: Int)
data class MyCustomType2(val City: String, val currency: Float)

sealed class Sealed3<T>(val obj: T) {
    // object ALPHA<T>: Sealed3()  // cant do generics for objects
    class BETA<T>(obj: T): Sealed3<T>(obj)
    data class GAMMA<T>(val obj2: T): Sealed3<T>(obj2)
    class DELTA<T, R>(obj: T, val obj2: R): Sealed3<T>(obj)
}