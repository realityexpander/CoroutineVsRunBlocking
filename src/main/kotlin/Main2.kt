import java.util.*
import kotlin.Comparator

// Testing Nested Classes with and without nested keyword
// Lazy vs lateinit
// collection operators: maxOf, maxBy, maxOfWith, Comparators
// takeif, takeUnless
// list -> map

data class Cat(val name:String, val age:Int)

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
    OuterClass1.x = x // mess with the lower bound of the random number generator
    OuterClass1.randomNumber()

    val rand = { OuterClass1.randomNumber() }

    val cats = listOf(
        Cat("tom", rand() ),
        Cat("Jim", rand()),
        Cat("Mandy", rand()),
        Cat("Whiskers", rand()),
        Cat("Mittens", rand()),
        Cat("Spot", rand()),
        Cat("cat", 5),
        Cat("dog", 5),
    )

    val cats2:List<Cat> = listOf()

    val codonTable = mapOf("ATT" to "Isoleucine", "CAA" to "Glutamine", "CGC" to "Arginine", "GGC" to "Glycine")
    val dnaFragment = "ATTCGCGGCCGCCAAXAF"
    val proteins = dnaFragment.chunked(3) { codon -> codonTable[codon.toString()] ?: "Unknown Codon"}
    println("proteins: $proteins")

    println("Oldest cat is: ${ cats.maxOf{ c -> c.age } } years old." )
    println("Oldest cat is: ${ cats.maxOf{ it.age } } years old." ) // identical to above
    println("Oldest cat of a null list of cats is: ${ cats2.maxOfOrNull{ it.age } } years old." ) // identical to above, except can return a null of none match
    println("Oldest cat name is ${ cats.maxByOrNull { c -> c.age }?.name }." ) // identical to above, except can return a null of none match
    println("Longest cat name is ${ cats.maxOfWithOrNull( {c1:Int, c2:Int -> if (c1==c2) 0 else if (c1>c2) 1 else -1} ) { c -> c.name.length } } characters." ) // same as above except uses comparator
    println("Longest cat name is ${ cats.maxOfWithOrNull<Cat, Cat>( 
            comparator = Comparator<Cat>{ c1:Cat, c2:Cat -> 
            if (c1.name.length==c2.name.length) 0 
                else if (c1.name.length>c2.name.length) 1 
                else -1
            }, 
            selector = { c -> c }
        )?.name }."
    ) // same as above except uses comparator
    println("Longest cat name is ${ cats.maxOfWithOrNull(
        { a, b -> val aLen=a.name.length; val bLen=b.name.length; if (aLen==bLen) 0 else if(aLen>bLen) 1 else -1 }) 
        { it }?.name }.") // same as above except on three lines

    val coin = rand()
    var over: Int? = coin.takeIf{ it >= 50}
    var under: Int? = coin.takeUnless{ it >= 50}
    println( over ?: "$coin is less than 50")
    println( under ?: "$coin is greater than 50")

    val str: String? = if(over==null) null else "over"
    println(str?.takeIf{ it.isNotEmpty() }?.uppercase(Locale.getDefault()) )

    val catsFlatMap = cats.flatMap { c -> listOf(c.name, c.age) }
    println("catsFlatMap=$catsFlatMap")
    val catsChunked = catsFlatMap.chunked(2) { c -> CatChunks(c[0] as String, c[1] as Int) }
    println("catsChunked=$catsChunked")
    val catMap = catsChunked.toMap()
    println("catMap=$catMap")
    val catKeyReversed = catMap.entries.associate { (k,v) -> v to k }
    val catKeyReversed2 = catMap.toList().map{ (k,v) -> v to k }.toMap()
    val catKeyReversed3 = catMap.entries.associateBy({ kv -> kv.value }, { kv -> kv.key })
    val catKeyReversed4 = catMap.entries.associateBy({ it.value }, { it.key })
    val catKeyReversed5: CatMapReversed = mutableMapOf()
        catMap.entries.associateByTo(catKeyReversed5, { it.value }, { it.key })
    println("catKeyReversed5=$catKeyReversed5, size=${catKeyReversed5.size}")

    // HashMap is same as Map, can only have one key for a value
    val catKeyReversedHash = CatHashMapReversed().also { hashMap ->
        catMap.entries.forEach { (k,v) -> hashMap[v] = k }
    }
    println("catKeyReversedHash=$catKeyReversedHash, size=${catKeyReversedHash.size}")

    val catMapReversedWithListValues: CatMapReversedWithListValues =
      catMap
        .toList()
        .groupBy{ (_,v) -> v } // group by the age(v), returns: Map<Age, Entries> & Entries is a List<Pair<Name,Age>>>
        .mapValues{ (_,entries) -> //entries }
            entries.map { (k,_) -> k  }.toMutableList() // Change the values for a key into a list of strings (cat name)
        }.toMutableMap()
    catMapReversedWithListValues[5].also { e -> e?.add("Kitten")}
    catMapReversedWithListValues[5]?.add("Another Kitten")
    catMapReversedWithListValues[5]?.remove("Kitten #3")
    catMapReversedWithListValues[5]?.remove("cat")
    catMapReversedWithListValues[5]?.remove("dog")

//    catMapReversedWithListValues[1].also { e -> if(e!=null) e.add("#####") else { e=mutableListOf("#$#-cant-do-this") } } // cant create an object in the null receiver


    catMapReversedWithListValues.addToList(6, "Hello kitty")
//    catMapReversedWithListValues[6].addToList3("Hello kitty") // Cant do this on a nullable receiver!!!

    println("catMapReversedWithListValues=$catMapReversedWithListValues, size=${catMapReversedWithListValues.size}")
}


fun CatMapReversedWithListValues.addToList( index: Int, catName: String) = run {
    if (this[index] == null) {
        this[index] = mutableListOf(catName)
    } else
        this[index]?.add(catName)
}

//// Impossible to pass a null receiver in and have it instantiated!
//fun MutableList<String>?.addToList3(catName: String) = run {
//    if (this == null) {
//        this = mutableListOf(catName)
//    } else
//        this.add(catName)
//}

typealias CatChunks = Pair<String, Int>
typealias CatMapReversed = MutableMap<Int, String>
typealias CatMap = Map<String, Int>
typealias CatHashMapReversed = HashMap<Int, String>
typealias CatMapReversedWithListValues = MutableMap<Int, MutableList<String>>

class OuterClass1(name: String) {
    val apple = "Apple"
    val number = 250

    lateinit var a: String // lateinit is only allowed on var
    val b: String by lazy { // lazy is only allowed on val
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