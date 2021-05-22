package dev.simran.routes

import dev.simran.data.getNotesForUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.notesRoutes() {
    route("/notes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name

                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }
}