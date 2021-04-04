package com.example.trakr.viewmodels

import androidx.lifecycle.ViewModel
import com.example.trakr.models.TimeEntry
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Duration
import java.time.temporal.ChronoUnit
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

    fun streamTimeEntriesToday(
        callback: (timeEntries: List<TimeEntry>?, error: FirebaseFirestoreException?) -> Unit
    ) {
        val startOfDayTimestamp = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        timeEntriesRef
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

    fun getHistoryUntil(
        endDateTime: LocalDateTime,
        callback: (days: HashMap<LocalDate, MutableList<TimeEntry>>?, error: FirebaseFirestoreException?) -> Unit
    ) {
        timeEntriesRef
            .orderBy("startTime", Query.Direction.DESCENDING)
            .endBefore(endDateTime.toEpochSecond(ZoneOffset.UTC))
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val days = hashMapOf<LocalDate, MutableList<TimeEntry>>()
                    value.documents.forEach {
                        val timeEntry = TimeEntry.fromDoc(it)
                        val localDate = timeEntry.startTime.toLocalDate()
                        if (days.containsKey(localDate)) {
                            days[localDate]!!.add(timeEntry)
                        } else {
                            days[localDate] = mutableListOf(timeEntry)
                        }
                    }
                    callback(days, error)
                } else {
                    callback(null, error)
                }
            }
    }
}