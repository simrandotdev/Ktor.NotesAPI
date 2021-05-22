package dev.simran.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val title: String,
    val content: String,
    val date: Long,
    var owners: MutableList<String> = ArrayList(),
    val color: String,
    @BsonId
    val id: String = ObjectId().toString()
)