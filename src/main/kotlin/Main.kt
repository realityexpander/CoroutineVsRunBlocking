
import kotlinx.coroutines.*
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

//            // Async await with cancellation
//            try {
//                asyncAwaitDoBothOperations()
//            } catch (e: Exception) {
//                log("$e") // will continue running coroutine
//            }
//            log("finished top-level runblocking")


            // Async Await with lazy start
            val networkRequest = async(start = CoroutineStart.LAZY) { networkRequest9() }
            //networkRequest.start() // just starts the coroutine
            log(networkRequest.await())

        }

//        asyncAwaitExampleBlocking()

//        networkRequest8()

    }

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
        delay(2500)
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
