package com.example.trakr.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trakr.R
import com.example.trakr.databinding.ViewDayBinding
import com.example.trakr.models.TimeEntry
import java.time.LocalDate

class DayRecyclerViewAdapter : RecyclerView.Adapter<DayRecyclerViewAdapter.ViewHolder>() {
    var days: HashMap<LocalDate, List<TimeEntry>> = hashMapOf()
        set(newDays) {
            field = newDays
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDayBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_day, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = days.keys.elementAt(position)
        val timeEntries = days[date]!!
        holder.binding.date = date
        holder.binding.timeEntriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TimeEntryRecyclerViewAdapter()
            (adapter as TimeEntryRecyclerViewAdapter).timeEntries = timeEntries
        }
    }

    override fun getItemCount(): Int = days.size

    inner class ViewHolder(val binding: ViewDayBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}