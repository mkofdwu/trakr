package com.example.trakr.models

import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.HashMap

data class User(
    val id: String,
    var username: String,
    var photoURL: String?,
    var activeTimeEntry: TimeEntry?,
    val colors: List<Int>,
    val createdAt: LocalDateTime,
) {
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            Pair("username", username),
            Pair("photoURL", photoURL),
            Pair("currentTimeEntry", activeTimeEntry?.toHashMap()),
            Pair("colors", colors),
            Pair("createdAt", createdAt.toEpochSecond(ZoneOffset.UTC))
        )
    }

    companion object {
        @JvmStatic
        fun fromDoc(doc: DocumentSnapshot): User {
            val map = doc.data!!
            return User(
                doc.id,
                map["username"] as String,
                map["photoURL"] as String?,
                if (map["currentTimeEntry"] != null)
                    TimeEntry.active(map["currentTimeEntry"] as HashMap<String, Any>)
                else null,
                map["colors"] as List<Int>,
                LocalDateTime.ofEpochSecond(map["createdAt"] as Long, 0, ZoneOffset.UTC)
            )
        }
    }
}