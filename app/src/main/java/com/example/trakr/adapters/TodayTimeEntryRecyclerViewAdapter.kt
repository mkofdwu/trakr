package com.example.trakr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.databinding.ViewTodayTimeEntryBinding
import com.example.trakr.models.TimeEntry

class TodayTimeEntryRecyclerViewAdapter(
    private val onReplayTimeEntry: (timeEntry: TimeEntry) -> Unit,
    private val onEditTimeEntry: (timeEntry: TimeEntry) -> Unit
) :
    RecyclerView.Adapter<TodayTimeEntryRecyclerViewAdapter.ViewHolder>() {
    var timeEntries: List<TimeEntry> = listOf()
        set(newTimeEntries) {
            field = newTimeEntries
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewTodayTimeEntryBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_today_time_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeEntry = timeEntries[position]
        holder.binding.timeEntry = timeEntry
        holder.binding.replayBtn.setOnClickListener { onReplayTimeEntry(timeEntry) }
        holder.binding.editBtn.setOnClickListener { onEditTimeEntry(timeEntry) }
    }

    override fun getItemCount(): Int = timeEntries.size

    inner class ViewHolder(val binding: ViewTodayTimeEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}