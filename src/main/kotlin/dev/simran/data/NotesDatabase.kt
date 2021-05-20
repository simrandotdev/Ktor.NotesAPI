package dev.simran.data

import dev.simran.data.collections.Note
import dev.simran.data.collections.User
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private const val DATABASE_NAME = "NotesDatabase"
private val client = KMongo.createClient("mongodb+srv://adminuser:adminuser@cluster0.mg0dr.mongodb.net/$DATABASE_NAME?retryWrites=true&w=majority").coroutine
private val database = client.getDatabase(DATABASE_NAME)
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users
                .insertOne(user)
                .wasAcknowledged()
}