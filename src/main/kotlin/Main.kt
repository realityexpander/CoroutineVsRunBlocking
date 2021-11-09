import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

private fun log(msg:String) = println("[${Thread.currentThread().name}] $msg")

fun main(args: Array<String>) {
//    val args2: Array<String> = Array(1) { "hello" }
    Main().main(args)
}

class Main() {

    fun main(args: Array<String>) {
        // Try adding program arguments via Run/Debug configuration.
        // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
        println("Program arguments: ${args.joinToString()}")
        println("Num args=${args.size}")

        runBlocking {
            networkRequest()
        }

    }

    private suspend fun networkRequest() {

        val time = measureTimeMillis {
            val job = GlobalScope.launch(Dispatchers.IO) {
                runBlocking {
                    log("Staring runBlocking")
                    delay(1000)
                    log("runBLocking...")
                    delay(1000)
                }

                coroutineScope {
                    log("Staring coroutineScope...")
                    delay(1000)
                    log("coroutingScope...")
                    delay(1000)
                }
            }

            job.join()
            log("Done!")
        }


        println("Time: $time")

    }

}
