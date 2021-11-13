
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

// Config for Android Studio: https://www.youtube.com/watch?v=a_pL0asAP3U
// This video: Kotlin Coroutines: coroutineScope vs. runBlocking (Tutorial) https://www.youtube.com/watch?v=k_xRxXoimSw

private fun log(msg:String) = println("[${Thread.currentThread().name}] $msg")

fun main(args: Array<String>) {
//    val args2: Array<String> = Array(1) { "hello" }
    Main().main(args)
}

@OptIn(DelicateCoroutinesApi::class)
class Main() {

    fun main(args: Array<String>) {
        // Try adding program arguments via Run/Debug configuration.
        // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
        println("Program arguments: [${args.joinToString()}]")
        println("Num args=${args.size}")

        runBlocking {
//            networkRequest()
//            println()
//            networkRequest2()

//            networkRequest3()
//            networkRequest4()
//            networkRequest5()
//            networkRequest6()
//            networkRequest7()


            //////////////////////////////////

//            // Async await with cancellation
//            try {
//                asyncAwaitDoBothOperations()
//            } catch (e: Exception) {
//                log("error $e") // will continue running coroutine
//            }
//            log("finished top-level runblocking")


//            // Async Await with lazy start
//            val networkRequest = async(start = CoroutineStart.LAZY) { networkRequest9() }
//            //networkRequest.start() // just starts the coroutine
//            log(networkRequest.await())

            ////////////////////////////////////

        }

//        asyncAwaitExampleBlocking()

//        networkRequest8()

//        runFlow1()
//        runFlow2()
//        runFlow3()

//        println(".buffer() =")
//        bufferExample()
//        println()
//        println(".conflate() =")
//        conflateExample()

//        zipCombineOperatorExample()
        flatMapConcatExample()


    }

    /// FLOW ///

    // Basic flow
    fun runFlow1() {

        runBlocking {
            println("Receiving numbers")
            sendNumbers1().collect {
                println("Receive number $it")
            }
            println("Finished.")
        }

    }

    // Transforms and filters
    fun runFlow2() {

        // cant use .also

        runBlocking {
            println("Receiving numbers")
            (1..10).asFlow()
                .map {
                    delay(300)
                    "Number $it"
                }
                .map {
                    it.substringAfter(" ").toInt()
                }
                .onEach {
                    it * 2
                }
                .filter{ it % 2 == 0 }
                .transform {
                    emit("The thing is $it")
                }
                .collect {
                    println("Received $it")
                }

            println("Finished.")
        }
    }


    // Exceptions and withIndex
    fun runFlow3() {

        runBlocking {
            println("Receiving numbers")
            (1..10).asFlow()
                .map {
                    delay(300)
                    it
                }
                .onEach {
                    check(it != 7)
                }
                .withIndex()
                .map {
                    println("${it.index}. value = ${it.value}")
                    it.value
                }
                .catch { e->
                    println("Caught exception $e")
                }
                .onStart {
                    println("Started...")
                }
                .onCompletion {
                    println("Flow completed!")
                }
                .collect {
                    println("Received $it")
                }

            println("Finished.")
        }
    }

    // Various generators
    fun sendNumbers1(): Flow<Int> = flow {
        val primesList = listOf<Int>(1,2,3,4,5,6,8)
        primesList.forEach {
            delay(it.toLong())
            emit(it)
        }
    }
    fun sendNumbers2() = flowOf(1,2,3,4,5)
    fun sendNumbers3() = listOf(1,2,3,4,5).asFlow()




    ///////////////////////////

    //// Buffer vs Conflate ///

    // https://www.youtube.com/watch?v=VJofY3ESaNg

    fun bufferExample()  = runBlocking {
        val time = measureTimeMillis {
            getFlow()
                .buffer(10) // creates a new coroutine, collects all values and holds them
                .collect {
                    delay(300) // pretend processing
                    println("collect $it")
                }
        }

        println("Time = $time")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun conflateExample()  = runBlocking {
        val time = measureTimeMillis {
            flowOnDispatcherDefault()
                .conflate() // when you only care about recent values (just get latest)
//                .take(2)
//                .transformWhile{ it ->
//                    println(it)
//                    emit(it<6)
//                    (it < 6)
//                }
//                .map {
//                    println(it)
//                }
                .onCompletion {
                    println("Flow completed!")
                }
                .collect {
                    delay(300) // pretend processing
                    println("collect $it")
                }
        }

        println("Time = $time")
    }

    fun getFlow() = flow {
        for( value in 1..10 ) {
            delay(100) // pretend computation / network
            println("emit flow $value")
            emit(value)
        }
    }

    fun getFlowStrings(value: Int) = flow {
        emit("First emitted $value")
        delay(300)
        emit("Second emitted $value")
    }

    fun flowOnDispatcherDefault() = flow {
//        withContext(Dispatchers.Default) { // Dont use this
            for( value in 1..10 ) {
                delay(100) // pretend computation / network
                println("emit flow $value")
                emit(value)
            }
    }.flowOn(Dispatchers.Default)


    fun zipCombineOperatorExample() = runBlocking {
        val flow1 = (1..5).asFlow()
            .onEach{ delay(100) }
        val flow2 = flowOf("one", "two", "three", "four", "five", "six")
            .onEach{ delay(200) }
        var startTime = System.currentTimeMillis()

        // zip waits for the value to arrive, the emits
        flow1.zip(flow2) { a, b ->
            "value $a -> $b"
        }.collect {
            println("$it zip ${System.currentTimeMillis() - startTime}ms")
        }

        println()
        startTime = System.currentTimeMillis()

        // combine emits the latest of either values
        flow1.combine(flow2) { a, b ->
            "value $a -> $b"
        }.collect {
            println("$it combine ${System.currentTimeMillis() - startTime}ms")
        }
    }

    fun flatMapConcatExample() = runBlocking {
        (1..5).asFlow()
            .onEach{
                delay(300)
            }
            .flatMapConcat {
                getFlowStrings(it)
            }
            .onEach {

            }
            .collect {
                println(it)
            }
    }

    //////////////////////////////


    /// COROUTINES ///

    // This one uses runBlocking, it ignores cancellations
    private suspend fun networkRequest() {
        val time = measureTimeMillis {
            val job = GlobalScope.launch(Dispatchers.IO) {
                runBlocking {
                    log("Starting runBlocking")
                    delay(1000)
                    log("runBLocking...")
                    delay(1000)
                }

                coroutineScope {
                    log("Starting coroutineScope...")
                    delay(1000)
                    log("coroutineScope...")
                    delay(1000)
                }
            }

            delay(400)
            log("Cancelling job")
            job.cancel()

            job.join()
            log("Done!")
        }

        println("networkRequest 1 Time: $time")
    }

    // This one uses coroutine scope so its cancellable
    private suspend fun networkRequest2() {
        val time = measureTimeMillis {
            val job = GlobalScope.launch(Dispatchers.IO) {
                coroutineScope {
                    log("Starting coroutineScope 1")
                    delay(1000)
                    log("coroutineScope 1...")
                    delay(1000)
                }

                coroutineScope {
                    log("Starting coroutineScope 2...")
                    delay(1000)
                    log("coroutineScope 2...")
                    delay(1000)
                }
            }

            delay(400)
            log("Cancelling job")
            job.cancel()

            job.join()
            log("Done!")
        }

        println("networkRequest 2 Time: $time")
    }

    private suspend fun networkRequest3() {
        GlobalScope.launch {
            log("Making network request 1")
            for (i in 1..3) {
                delay(1000)
                println("First network req: $i")
            }
            log("First network req done.")
        }

        GlobalScope.launch {
            log("Making network request 2")
            for (i in 1..3) {
                delay(1000)
                println("Second network req: $i")
            }
            log("Second network req done.")
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        Thread.sleep(4000) // Wait for program completion

        log("Done.")
    }

    // Sequential blocking and various dispatchers
    suspend fun networkRequest4() {
        runBlocking {
            launch {
                log("first - no scope") // runs in main (inherits scope)
            }
            delay(1000)

            // Good for no cpu nor update UI
            launch(Dispatchers.Unconfined) {
                log("second - .unconfined") // runs on main
                delay(100)
                log("second 2 - .unconfined") // runs on defaultExecutor
            }
            delay(1000)

            // Same as launching in GlobalScope, good for CPU intensive ops
            launch(Dispatchers.Default) {
                log("third - .default") // runs of worker-1
            }
            delay(1000)

            // Make sure thread is destroyed after not needed
            launch(newSingleThreadContext("Banana Bread")) {
                log("fourth - newSingleThread") // runs on Banana Bread
            }
            delay(1000)
        }
    }

    // Cancel and Join simple example
    private suspend fun networkRequest5() {
        val job = GlobalScope.launch {
            log("Job 1: Making network request")
            for (i in 1..5) {
                delay(1000)
                println("Job 1: Waiting...: $i")
            }
            log("Job1: network req done.")
        }

        val job2 = GlobalScope.launch {
            log("Job 2: Making network request")
            for (i in 1..5) {
                delay(1000)
                println("Job 2: Waiting...: $i")
            }
            log("Job2: network req done.")
        }

        delay(2000)
        job.cancelAndJoin()
        job2.join()

        log("Done")
    }

    // Cancel with timeout example
    private suspend fun networkRequest6() {
        try {
            withTimeout(5000) {
                val job = GlobalScope.launch {
                    log("Job 1: Making network request")
                    for (i in 1..5) {
                        delay(3000)
                        println("Job 1: Waiting...: $i")
                    }
                    log("Job1: network req done.")
                }

                job.join()
                log("network request successful")
            }

            log("Done")
        } catch (e:Exception) {
            log(e.toString())
            log("network request cancelled")
        }
    }

    // Cancel with timeoutOrNull example
    private suspend fun networkRequest7() {
        withTimeoutOrNull(5000) {
            val job = GlobalScope.launch {
                log("Job 1: Making network request")
                for (i in 1..5) {
                    delay(3000)
                    println("Job 1: Waiting...: $i")
                }
                log("Job1: network req done.")
            }

            job.join()
            log("network request successful")
        } // Will end with a null/no-op

        log("Done")
    }

    // Cancellation example 1
    private fun networkRequest8() {
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i<5 && isActive) { // use isActive to check for cancellation
                    // computation loop, waste CPU
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: Im sleeping ${i++}")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300)
            println("main: Im tired of waiting!")
            job.cancelAndJoin()
            println("main: now I can quit")
        }
    }

    //////////////////////////////////
    // Async and Await

    private fun asyncAwaitExampleBlocking() {
        runBlocking {
            // Async & Await example
            val time = measureTimeMillis {
                val sum = async { addNumbers(10, 12) }
                val product = async { multiplyNumbers(10, 12) }

                log("the sum of addNumbers: ${sum.await()}")
                log("the product of multiplyNumbers: ${product.await()}")
            }
            log("Completed $time")
        }
    }

    private suspend fun asyncAwaitDoBothOperations() = coroutineScope {
        val time = measureTimeMillis {
            val sum = async { addNumbers(10, 12) }
            val productWithErrors = async { multiplyNumbersWithErrors(10, 12) }

            log("the sum of addNumbers: ${sum.await()}")

            log("the product of multiplyNumbers: ${productWithErrors.await()}") // will never print and cancels the coroutine
//            GlobalScope.launch {
//                val productWithErrors2 = async { multiplyNumbersWithErrors(20, 32) }
//                log("the product of multiplyNumbers2: ${productWithErrors2.await()}")
//            }

            log("After error productWithErrors ")
        }
        log("Completed $time") // never completes
    }

    private suspend fun addNumbers(n: Int, n2: Int): Int {
        delay(2000)
        return n + n2
    }

    private suspend fun multiplyNumbers(n: Int, n2: Int): Int {
        delay(2500)
        return n * n2
    }

    private suspend fun multiplyNumbersWithErrors(n: Int, n2: Int): Int {
        delay(500)
        throw RuntimeException()
        //return n * n2
    }

    private suspend fun networkRequest9(): String {
        log("Making network request 1")
        for (i in 1..3) {
            delay(1000)
            println("First network req: $i")
        }
        delay(1000)
        log("First network req done.")

        return "Network data"
    }

}
