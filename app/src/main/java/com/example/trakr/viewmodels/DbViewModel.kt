package com.example.trakr.viewmodels

import androidx.lifecycle.ViewModel
import com.example.trakr.models.TimeEntry
import com.google.firebase.firestore.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

class DbViewModel : ViewModel() {
    private lateinit var timeEntriesRef: CollectionReference

    fun setUserId(userId: String) {
        timeEntriesRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("timeEntries")
    }

    fun addActiveTimeEntry(activeTimeEntry: TimeEntry) {
        val timeEntry = activeTimeEntry.copy()
        val seconds = ChronoUnit.SECONDS.between(timeEntry.startTime, LocalDateTime.now())
        timeEntry.duration = seconds.toDuration(TimeUnit.SECONDS)
        timeEntriesRef.add(timeEntry.toHashMap())
    }

    fun deleteTimeEntry(id: String) {
        timeEntriesRef.document(id).delete()
    }

    fun updateTimeEntry(timeEntry: TimeEntry) {
        timeEntriesRef.document(timeEntry.id!!).update(timeEntry.toHashMap())
    }

    fun listenToTimeEntriesToday(
        callback: (timeEntries: List<TimeEntry>?, error: FirebaseFirestoreException?) -> Unit
    ): ListenerRegistration {
        val startOfDayTimestamp = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return timeEntriesRef
            .orderBy("startTime", Query.Direction.DESCENDING)
            .endBefore(startOfDayTimestamp)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val timeEntries = value.documents.map { TimeEntry.fromDoc(it) }
                    callback(timeEntries, error)
                } else {
                    callback(null, error)
                }
            }
    }

    fun mostTimeSpent(
        endDateTime: LocalDateTime,
        num: Int,
        callback: (titlesToTimeSpent: Map<String, Long>) -> Unit
    ) {
        timeEntriesRef
            .orderBy("startTime", Query.Direction.DESCENDING)
            .endBefore(endDateTime.toEpochSecond(ZoneOffset.UTC))
            .get().addOnSuccessListener { snapshot ->
                val titlesToDuration = hashMapOf<String, Long>()
                snapshot.documents.forEach { doc ->
                    val title = doc.data!!["title"] as String
                    val duration = doc.data!!["duration"] as Long
                    if (titlesToDuration.containsKey(title)) {
                        titlesToDuration[title]!!.plus(duration)
                    } else {
                        titlesToDuration[title] = duration
                    }
                }
                val sorted =
                    titlesToDuration.toList().sortedBy { (_, value) -> -value }
                        .subList(0, num.coerceAtMost(titlesToDuration.size))
                        .toMap()
                callback(sorted)
            }
    }

    fun getHistory(
        dateTime: LocalDateTime,
        untilOld: Boolean,
        maxTimeEntries: Int, // total amount of time entries to load
        callback: (days: SortedMap<LocalDate, List<TimeEntry>>, numTimeEntries: Int, otherDateTime: LocalDateTime?, error: FirebaseFirestoreException?) -> Unit
    ) {
        timeEntriesRef
            .orderBy(
                "startTime",
                if (untilOld) Query.Direction.DESCENDING else Query.Direction.ASCENDING
            )
            .startAfter(dateTime.toEpochSecond(ZoneOffset.UTC))
            .limit(maxTimeEntries.toLong())
            .addSnapshotListener { value, error ->
                if (value != null && value.documents.isNotEmpty()) {
                    val days = hashMapOf<LocalDate, List<TimeEntry>>()
                    value.documents.forEach {
                        val timeEntry = TimeEntry.fromDoc(it)
                        val localDate = timeEntry.startTime.toLocalDate()
                        if (days.containsKey(localDate)) {
                            (days[localDate] as MutableList).add(timeEntry)
                        } else {
                            days[localDate] = mutableListOf(timeEntry)
                        }
                    }
                    val sortedDays = days.toSortedMap(compareByDescending { it })
                    callback(
                        sortedDays,
                        value.documents.size,
                        if (untilOld) sortedDays.lastKey().atStartOfDay() else sortedDays.firstKey()
                            .atStartOfDay(),
                        error
                    )
                } else {
                    callback(sortedMapOf(), 0, null, error)
                }
            }
    }
}