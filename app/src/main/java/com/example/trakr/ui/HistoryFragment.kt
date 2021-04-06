package com.example.trakr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.adapters.DayRecyclerViewAdapter
import com.example.trakr.databinding.FragmentHistoryBinding
import com.example.trakr.viewmodels.DbViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val dbViewModel: DbViewModel by activityViewModels()

    private val daysListAdapter = DayRecyclerViewAdapter()

    private var daysLoaded = 0
    private var loadedEverything = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, null, false)
        binding.fragment = this
        binding.daysList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = daysListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL)) {
                        loadMoreDays()
                    }
                }
            })
        }
        return binding.root
    }

    private fun loadMoreDays() {
        if (loadedEverything) return
        val endDateTime = LocalDateTime.now().minusDays((daysLoaded + 4).toLong())
        dbViewModel.getHistory(LocalDateTime.now(), endDateTime) { days, error ->
            if (error != null) {
                Snackbar.make(
                    requireView(),
                    "Failed to fetch history: ${error.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                if (days.size - daysListAdapter.days.size < 4) {
                    loadedEverything = true
                }
                daysListAdapter.days = days
                daysLoaded = days.size
            }
        }
    }

    fun back() {
        requireView().findNavController().navigateUp()
    }

    fun goToSearch() {

    }
}