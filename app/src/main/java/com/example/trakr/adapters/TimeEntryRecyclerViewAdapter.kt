package com.example.trakr.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.trakr.R
import com.example.trakr.databinding.ViewTimeEntryBinding
import com.example.trakr.models.TimeEntry

class TimeEntryRecyclerViewAdapter :
    RecyclerView.Adapter<TimeEntryRecyclerViewAdapter.ViewHolder>() {
    var timeEntries: List<TimeEntry> = listOf()
        set(newTimeEntries) {
            field = newTimeEntries
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewTimeEntryBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_time_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeEntry = timeEntries[position]
        holder.binding.timeEntry = timeEntry
    }

    override fun getItemCount(): Int = timeEntries.size

    inner class ViewHolder(val binding: ViewTimeEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}