package dev.simran.routes

import dev.simran.data.collections.Note
import dev.simran.data.deleteNote
import dev.simran.data.getNotesForUser
import dev.simran.data.requests.UpdateOwnerRequest
import dev.simran.data.saveNote
import dev.simran.data.updateOwner
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
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

            post {

                val email = call.principal<UserIdPrincipal>()!!.name
                val note = try {
                    call.receive<Note>()
                } catch (ex: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                if(note.owners != null) {
                    note.owners.add(email)
                } else {
                    note.owners = mutableListOf(email)
                }

                if(saveNote(note)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }

            delete("/{id}") {
                val email = call.principal<UserIdPrincipal>()!!.name
                val idToDelete = call.parameters["id"].toString()

                if (deleteNote(idToDelete, email)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Not able to delete the Note.")
                }
            }

            put("/{id}") {
                val email = call.principal<UserIdPrincipal>()!!.name
                val noteId = call.parameters["id"].toString()

                val newOwner = try {
                    call.receive<UpdateOwnerRequest>()
                } catch (ex: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if(updateOwner(noteId, newOwner.email, email)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Not able to update owner of the Note.")
                }
            }
        }
    }
}