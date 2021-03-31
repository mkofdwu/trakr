package com.example.trakr.models

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.HashMap

data class User(
    val username: String,
    val photoURL: String,
    val createdAt: LocalDateTime,
    val currentTimeEntry: TimeEntry?
) {
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            Pair("username", username),
            Pair("photoURL", photoURL),
            Pair("createdAt", Timestamp(createdAt.toEpochSecond(ZoneOffset.UTC), createdAt.nano)),
            Pair("currentTimeEntry", currentTimeEntry?.toHashMap())
        )
    }

    companion object {
        @JvmStatic
        fun fromHashMap(map: HashMap<String, Any?>): User {
            val timestamp = map["createdAt"] as Timestamp
            val createdAt = LocalDateTime.ofEpochSecond(
                timestamp.seconds,
                timestamp.nanoseconds,
                ZoneOffset.UTC
            )
            return User(
                map["username"] as String,
                map["photoURL"] as String,
                createdAt,
                if (map["currentTimeEntry"] != null)
                    TimeEntry.fromHashMap(map["currentTimeEntry"] as HashMap<String, Any>)
                else null
            )
        }
    }
}