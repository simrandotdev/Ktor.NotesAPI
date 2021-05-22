package dev.simran.data

import dev.simran.data.collections.Note
import dev.simran.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.not
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue


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

suspend fun userIsOwnerOf(email: String, noteId: String): Boolean {
    return notes.findOne(Note::id eq noteId, Note::owners contains email) != null
}


suspend fun getNotesForUser(email: String): List<Note> {
    val notesList = notes.find(Note::owners contains email)
    return notesList.toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notes.findOneById(note.id) != null
    return if(noteExists) {
        notes.updateOneById(note.id, note).wasAcknowledged()
    } else {
        notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun deleteNote(id: String, ownersEmail: String): Boolean {
    val noteToDelete = notes.findOneById(id)
    if(noteToDelete != null && userIsOwnerOf(ownersEmail, id)) {
        if(noteToDelete.owners.size > 1) {
            val newOwners = noteToDelete.owners - ownersEmail
            val updatedResults = notes.updateOne(Note::id eq id, setValue(Note::owners, newOwners))
            return updatedResults.wasAcknowledged()
        }
        return notes.deleteOneById(id).wasAcknowledged()
    }
    return false
}

suspend fun updateOwner(noteId: String, newOwnerEmail: String, orignalOwnerEmail: String): Boolean {
    if (!userIsOwnerOf(orignalOwnerEmail, noteId)) return false

    val note = notes.findOneById(noteId)
    note?.owners?.add(newOwnerEmail)
    val newOwners = note?.owners
    return notes.updateOne(Note::id eq noteId, setValue(Note::owners, newOwners)).wasAcknowledged()
}