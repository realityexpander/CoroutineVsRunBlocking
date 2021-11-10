
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// Config for Android Studio: https://www.youtube.com/watch?v=a_pL0asAP3U
// This video: Kotlin Coroutines: coroutineScope vs. runBlocking (Tutorial) https://www.youtube.com/watch?v=k_xRxXoimSw

private fun log(msg:String) = println("[${Thread.currentThread().name}] $msg")

fun main(args: Array<String>) {
//    val args2: Array<String> = Array(1) { "hello" }
    Main().main(args)
}

class Main() {

    fun main(args: Array<String>) {
        // Try adding program arguments via Run/Debug configuration.
        // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
        println("Program arguments: [${args.joinToString()}]")
        println("Num args=${args.size}")

        runBlocking {
            networkRequest()
            println()
            networkRequest2()
        }

    }

    // This one uses runBlocking, it ignores cancellations
    @OptIn(DelicateCoroutinesApi::class)
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
    @OptIn(DelicateCoroutinesApi::class)
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

}
