package com.example.trakr.models

import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration

data class TimeEntry(
    val id: String?,
    val title: String,
    val startTime: LocalDateTime,
    val duration: Duration,
    val color: Int
) {
    fun exists() = id != null

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            Pair("title", title),
            Pair("startTime", startTime.toEpochSecond(ZoneOffset.UTC)),
            Pair("duration", duration.inSeconds),
            Pair("color", color)
        )
    }

    companion object {
        @JvmStatic
        fun fromDoc(doc: DocumentSnapshot): TimeEntry {
            val map = doc.data!!
            return TimeEntry(
                doc.id,
                map["title"] as String,
                LocalDateTime.ofEpochSecond(map["startTime"] as Long, 0, ZoneOffset.UTC),
                (map["duration"] as Int).toDuration(TimeUnit.SECONDS),
                map["color"] as Int
            )
        }

        @JvmStatic
        fun active(map: HashMap<String, Any>): TimeEntry {
            return TimeEntry(
                null,
                map["title"] as String,
                LocalDateTime.ofEpochSecond(map["startTime"] as Long, 0, ZoneOffset.UTC),
                (map["duration"] as Int).toDuration(TimeUnit.SECONDS),
                map["color"] as Int
            )
        }
    }
}