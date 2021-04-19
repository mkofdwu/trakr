package com.example.trakr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.databinding.FragmentHistoryBinding
import com.example.trakr.ui.adapters.DayRecyclerViewAdapter
import com.example.trakr.viewmodels.DbViewModel
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view as TextView
}

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view as TextView
}

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val dbViewModel: DbViewModel by activityViewModels()

    private val daysListAdapter = DayRecyclerViewAdapter(this)

    private var startDateTime = LocalDateTime.now()
    private var endDateTime = LocalDateTime.now()
    private var timeEntriesLoaded = 0
    private var loadedUntilOldest = false
    private var loadedUntilNewest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, null, false)
        binding.fragment = this
        binding.daysList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter = daysListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(-1)) { // scroll up
                        loadOlderDays()
                    }
                    if (!recyclerView.canScrollVertically(1)) { // scroll down
                        loadNewerDays()
                    }
                }
            })
        }
        loadOlderDays()
        binding.dateSelector.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view as TextView)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                container.textView.setOnClickListener {
                    goToDay(day)
                }
            }
        }
        binding.dateSelector.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    container.textView.text =
                        month.yearMonth.month.name.toLowerCase().capitalize() + " " + month.year
                }
            }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        binding.dateSelector.setup(firstMonth, lastMonth, DayOfWeek.SUNDAY)
        binding.dateSelector.scrollToMonth(currentMonth)
        return binding.root
    }

    private fun loadNewerDays() {
        if (loadedUntilNewest) return
        dbViewModel.getHistory(
            startDateTime,
            false,
            timeEntriesLoaded + 20
        ) { days, numTimeEntries, otherDateTime, error ->
            if (error != null) {
                Snackbar.make(
                    requireView(),
                    "Failed to fetch history: ${error.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                if (numTimeEntries - timeEntriesLoaded < 20) {
                    loadedUntilNewest = true
                }
                daysListAdapter.days = days
                timeEntriesLoaded = numTimeEntries
                endDateTime = otherDateTime

                binding.daysList.visibility = if (numTimeEntries == 0) View.GONE else View.VISIBLE
                binding.placeholderContainer.visibility =
                    if (numTimeEntries == 0) View.VISIBLE else View.GONE
            }
        }
    }

    private fun loadOlderDays() {
        if (loadedUntilOldest) return
        dbViewModel.getHistory(
            endDateTime,
            true,
            timeEntriesLoaded + 20
        ) { days, numTimeEntries, otherDateTime, error ->
            if (error != null) {
                Snackbar.make(
                    requireView(),
                    "Failed to fetch history: ${error.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                if (numTimeEntries - timeEntriesLoaded < 20) {
                    loadedUntilOldest = true
                }
                daysListAdapter.days = days
                timeEntriesLoaded = numTimeEntries
                startDateTime = otherDateTime

                binding.daysList.visibility = if (numTimeEntries == 0) View.GONE else View.VISIBLE
                binding.placeholderContainer.visibility =
                    if (numTimeEntries == 0) View.VISIBLE else View.GONE
            }
        }
    }

    private fun goToDay(day: CalendarDay) {
        endDateTime = day.date.plusDays(1).atStartOfDay()
        timeEntriesLoaded = 0
        loadedUntilOldest = false

        binding.daysList.visibility = View.VISIBLE
        binding.dateSelector.visibility = View.GONE

        loadOlderDays()
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun goToSearch() {
        // TODO
    }

    fun goToSelectDate(initialDate: LocalDate) {
        binding.daysList.visibility = View.GONE
        binding.dateSelector.visibility = View.VISIBLE
        binding.dateSelector.scrollToDate(initialDate)
    }
}