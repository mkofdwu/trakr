package com.example.trakr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.databinding.ViewPastTimeEntryBinding
import com.example.trakr.models.TimeEntry

class PastTimeEntryRecyclerViewAdapter(private val indicatorOnClick: (timeEntry: TimeEntry) -> Unit) :
    RecyclerView.Adapter<PastTimeEntryRecyclerViewAdapter.ViewHolder>() {
    var timeEntries: List<TimeEntry> = listOf()
        set(newTimeEntries) {
            field = newTimeEntries
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewPastTimeEntryBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_past_time_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeEntry = timeEntries[position]
        holder.binding.timeEntry = timeEntry
        holder.binding.indicator.setOnClickListener {
            indicatorOnClick(timeEntry)
        }
    }

    override fun getItemCount(): Int = timeEntries.size

    inner class ViewHolder(val binding: ViewPastTimeEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}