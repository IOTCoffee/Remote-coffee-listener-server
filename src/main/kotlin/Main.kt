import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import sun.misc.Signal
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    var pid = "-1"
    var on = false
    if (args.isNotEmpty()) {
        pid = args[0]
        println("We got a pid!")
    }
    println("pid: $pid")

    Signal.handle(Signal("INT")) {
        println("HE'S DEAD JIM")
        Runtime.getRuntime().exec("kill -SIGTERM $pid")
        exitProcess(0)
    }

    embeddedServer(Netty, 80) {
        routing {
            get("/") {
                Runtime.getRuntime().exec("kill -SIGINT $pid")
                on = !on
                call.respond(HttpStatusCode.Accepted, "FLASH")
            }

            get("/ping") {
                if (on) {
                    call.respond(HttpStatusCode.Accepted, "{\"msg\": \"true\"}")
                } else {
                    call.respond(HttpStatusCode.Accepted, "{\"msg\": \"false\"}")
                }
            }
        }
        println("Server started 😎")
    }.start(wait = true)
}

