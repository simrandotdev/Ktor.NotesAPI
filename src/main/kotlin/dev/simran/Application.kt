package dev.simran

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.simran.plugins.*
import dev.simran.routes.loginRoute
import dev.simran.routes.registerRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Setting defaults headers
        install(DefaultHeaders)
        // Logging calls made to API
        install(CallLogging)
        // Setting Routing
        install(Routing) {
            registerRoute()
        }
        // Format in which we want to sent the response
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        configureRouting()
    }.start(wait = true)
}