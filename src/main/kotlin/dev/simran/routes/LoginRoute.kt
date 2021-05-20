package dev.simran.routes

import dev.simran.data.checkIfUserExists
import dev.simran.data.checkPasswordForEmail
import dev.simran.data.collections.User
import dev.simran.data.registerUser
import dev.simran.data.requests.AccountRequest
import dev.simran.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val isPasswordCorrect = checkPasswordForEmail(
                email = request.email.toLowerCase(),
                passwordToCheck = request.password
            )

            if(isPasswordCorrect) {
                call.respond(HttpStatusCode.OK, SimpleResponse(successful = true, message = "You are now logged in"))
            } else {
                call.respond(HttpStatusCode.Unauthorized, SimpleResponse(successful = false, message = "Email/Password is incorrect"))
            }
        }
    }
}