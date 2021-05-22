package dev.simran

import dev.simran.data.checkPasswordForEmail
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.simran.plugins.*
import dev.simran.routes.notesRoutes
import dev.simran.routes.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Setting defaults headers
        install(DefaultHeaders)
        // Logging calls made to API
        install(CallLogging)
        install(Authentication) {
            configureAuth()
        }
        // Setting Routing
        install(Routing) {
            registerRoute()
            notesRoutes()
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


private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Notes Server"
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password

            if(checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email)
            } else {
                null
            }
        }
    }
}