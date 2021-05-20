package dev.simran

import dev.simran.data.collections.User
import dev.simran.data.registerUser
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.simran.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Setting defaults headers
        install(DefaultHeaders)
        // Logging calls made to API
        install(CallLogging)
        // Setting Routing
        install(Routing)
        // Format in which we want to sent the response
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            registerUser(User("simran@gmail.com", "password123"))
        }


        configureRouting()
    }.start(wait = true)
}