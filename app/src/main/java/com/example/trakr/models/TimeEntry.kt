package com.example.trakr.models

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration

data class TimeEntry(
    var id: String?,
    var title: String,
    val startTime: LocalDateTime,
    var duration: Duration,
    var color: Int
) : Serializable {
    fun exists() = id != null

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            Pair("title", title),
            Pair("startTime", startTime.toEpochSecond(ZoneOffset.UTC)),
            Pair("duration", duration.inSeconds.toInt()),
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
                (map["duration"] as Long).toDuration(TimeUnit.SECONDS),
                (map["color"] as Long).toInt()
            )
        }

        @JvmStatic
        fun active(map: HashMap<String, Any>): TimeEntry {
            return TimeEntry(
                null,
                map["title"] as String,
                LocalDateTime.ofEpochSecond(map["startTime"] as Long, 0, ZoneOffset.UTC),
                (map["duration"] as Long).toDuration(TimeUnit.SECONDS),
                (map["color"] as Long).toInt()
            )
        }
    }
}