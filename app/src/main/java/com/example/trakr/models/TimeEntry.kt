package com.example.trakr.models

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration

data class TimeEntry(
    val title: String,
    val startTime: LocalDateTime,
    val duration: Duration,
    val color: Int
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            Pair("title", title),
            Pair("startTime", Timestamp(startTime.toEpochSecond(ZoneOffset.UTC), startTime.nano)),
            Pair("duration", duration.inSeconds),
            Pair("color", color)
        )
    }

    companion object {
        @JvmStatic
        fun fromHashMap(map: HashMap<String, Any>): TimeEntry {
            val timestamp = map["startTime"] as Timestamp
            val startTime = LocalDateTime.ofEpochSecond(
                timestamp.seconds,
                timestamp.nanoseconds,
                ZoneOffset.UTC
            )
            return TimeEntry(
                map["title"] as String,
                startTime,
                (map["duration"] as Int).toDuration(TimeUnit.SECONDS),
                map["color"] as Int
            )
        }
    }
}