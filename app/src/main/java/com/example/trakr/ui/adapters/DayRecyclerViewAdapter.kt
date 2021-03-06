package com.example.trakr.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.databinding.ViewDayBinding
import com.example.trakr.models.TimeEntry
import com.example.trakr.ui.fragments.HistoryFragment
import java.time.LocalDate
import java.util.*

class DayRecyclerViewAdapter(private val fragment: HistoryFragment) :
    RecyclerView.Adapter<DayRecyclerViewAdapter.ViewHolder>() {
    var days: SortedMap<LocalDate, List<TimeEntry>> = sortedMapOf()
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
        holder.binding.fragment = fragment
        holder.binding.date = date
        holder.binding.timeEntriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PastTimeEntryRecyclerViewAdapter {
                findNavController().navigate(
                    R.id.action_historyFragment_to_editTimeEntryFragment,
                    Bundle().apply {
                        putSerializable("timeEntry", it)
                    })
            }
            (adapter as PastTimeEntryRecyclerViewAdapter).timeEntries = timeEntries
        }
    }

    override fun getItemCount(): Int = days.size

    inner class ViewHolder(val binding: ViewDayBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}