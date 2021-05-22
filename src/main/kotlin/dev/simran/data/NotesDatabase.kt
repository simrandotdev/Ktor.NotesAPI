package dev.simran.data

import dev.simran.data.collections.Note
import dev.simran.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo


private const val DATABASE_NAME = "NotesDatabase"
private const val CONNECTION_STRING = "mongodb+srv://adminuser:adminuser@cluster0.mg0dr.mongodb.net/$DATABASE_NAME?retryWrites=true&w=majority"

private val client = KMongo.createClient(CONNECTION_STRING).coroutine
private val database = client.getDatabase(DATABASE_NAME)
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users
                .insertOne(user)
                .wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users
//        .findOne("{email: $email}") != null   // OR
        .findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password

    actualPassword.let {
        return actualPassword == passwordToCheck
    }

    return false
}

suspend fun getNotesForUser(email: String): List<Note> {
    val notesList = notes.find(Note::owners contains email)
    return notesList.toList()
}