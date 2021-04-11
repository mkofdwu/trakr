package com.example.trakr.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Format {
    companion object {
        @JvmStatic
        fun timeOfDay(dateTime: LocalDateTime): String {
            return dateTime.format(DateTimeFormatter.ofPattern("h.mm a"))
        }

        @JvmStatic
        fun date(dateTime: LocalDateTime): String {
            return dateTime.format(DateTimeFormatter.ofPattern("dd MMM"))
        }

        @JvmStatic
        fun timerDuration(duration: Long): String {
            val seconds = (duration % 60).toInt()
            val minutes = (duration % 3600 / 60).toInt()
            val hours = (duration / 3600).toInt()
            val f = { n: Int -> n.toString().padStart(2, '0') }
            return when {
                hours > 0 -> "${f(hours)}:${f(minutes)}:${f(seconds)}"
                minutes > 0 -> "${f(minutes)}:${f(seconds)}"
                else -> "${seconds}s"
            }
        }

        @JvmStatic
        fun abbrDuration(seconds: Long): String {
            if (seconds < 60) return "${seconds}s"
            if (seconds < 60 * 60) return "${seconds / 60}m"
            if (seconds < 60 * 60 * 24) return "${seconds / 60 / 60}h"
            return "${seconds / (60 * 60 * 24)}d"
        }
    }
}