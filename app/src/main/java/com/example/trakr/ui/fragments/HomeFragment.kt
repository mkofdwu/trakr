package com.example.trakr.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.adapters.TimeEntryRecyclerViewAdapter
import com.example.trakr.databinding.FragmentHomeBinding
import com.example.trakr.viewmodels.UserViewModel
import com.example.trakr.viewmodels.DbViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    private var durationTimer = Timer()

    private val timeEntriesListAdapter = TimeEntryRecyclerViewAdapter {
        findNavController().navigate(
            R.id.action_homeFragment_to_editTimeEntryFragment,
            Bundle().apply {
                putSerializable("timeEntry", it)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, null, false)
        binding.fragment = this
        val currentUser = userViewModel.getCurrentUser()
        binding.user = currentUser
        if (currentUser.activeTimeEntry != null) {
            durationTimer = Timer()
            durationTimer.schedule(object : TimerTask() {
                override fun run() {
                    val startTime = currentUser.activeTimeEntry!!.startTime
                    val duration = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now())
                    val seconds = (duration % 60).toInt()
                    val minutes = (duration % 3600 / 60).toInt()
                    val hours = (duration / 3600).toInt()
                    val f = { n: Int -> n.toString().padStart(2, '0') }
                    val str: String = when {
                        hours > 0 -> "${f(hours)}:${f(minutes)}:${f(seconds)}"
                        minutes > 0 -> "${f(minutes)}:${f(seconds)}"
                        else -> "${seconds}s"
                    }
                    requireActivity().runOnUiThread {
                        binding.activeTimeEntry.durationText.text = str
                    }
                }
            }, 0, 1000)
        }
        binding.timeEntriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timeEntriesListAdapter
        }
        dbViewModel.streamTimeEntriesToday { timeEntries, error ->
            if (timeEntries != null && timeEntries.isNotEmpty()) {
                timeEntriesListAdapter.timeEntries = timeEntries
                binding.timeEntriesList.visibility = View.VISIBLE
                binding.placeholderContainer.visibility = View.GONE
            } else {
                binding.timeEntriesList.visibility = View.GONE
                if (currentUser.activeTimeEntry == null) {
                    binding.placeholderContainer.visibility = View.VISIBLE
                }
            }
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        durationTimer.cancel()
        durationTimer.purge()
    }

    fun finishActiveTimeEntry() {
        // first stop the timer
        durationTimer.cancel()
        durationTimer.purge()
        // then update data
        val currentUser = userViewModel.getCurrentUser()
        dbViewModel.addActiveTimeEntry(currentUser.activeTimeEntry!!)
        currentUser.activeTimeEntry = null
        userViewModel.updateUser("activeTimeEntry", null)
        binding.invalidateAll()
    }

    fun deleteActiveTimeEntry() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete entry?")
        builder.setMessage("Are you sure you want to delete this time entry? You can't undo this action.")
        builder.setPositiveButton("Ok") { _, _ ->
            durationTimer.cancel()
            durationTimer.purge()
            val currentUser = userViewModel.getCurrentUser()
            currentUser.activeTimeEntry = null
            userViewModel.updateUser("activeTimeEntry", null)
            if (binding.timeEntriesList.adapter!!.itemCount == 0) {
                binding.placeholderContainer.visibility = View.VISIBLE
            }
            binding.invalidateAll()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    fun goToNewTimeEntry() {
        findNavController().navigate(R.id.action_homeFragment_to_newTimeEntryFragment)
    }

    fun goToHistory() {
        findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
    }

    fun goToSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
    }
}