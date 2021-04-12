package com.example.trakr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trakr.R
import com.example.trakr.databinding.FragmentHomeBinding
import com.example.trakr.models.TimeEntry
import com.example.trakr.ui.adapters.TodayTimeEntryRecyclerViewAdapter
import com.example.trakr.utils.Format
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    private var durationTimer = Timer()
    private lateinit var listenerRegistration: ListenerRegistration

    private val timeEntriesListAdapter = TodayTimeEntryRecyclerViewAdapter({
        durationTimer.cancel()
        durationTimer.purge()
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser.activeTimeEntry != null) {
            dbViewModel.addActiveTimeEntry(currentUser.activeTimeEntry!!)
        }
        currentUser.activeTimeEntry = TimeEntry(
            null,
            it.title,
            LocalDateTime.now(),
            Duration.ZERO,
            it.color
        )
        userViewModel.updateUser("activeTimeEntry", currentUser.activeTimeEntry!!.toHashMap())
        binding.invalidateAll()
        setupTimer()
    }, {
        findNavController().navigate(
            R.id.action_homeFragment_to_editTimeEntryFragment,
            Bundle().apply {
                putSerializable("timeEntry", it)
            })
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, null, false)
        binding.fragment = this
        val currentUser = userViewModel.getCurrentUser()
        binding.user = currentUser
        if (currentUser.activeTimeEntry != null) {
            setupTimer()
        }
        binding.timeEntriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timeEntriesListAdapter
        }
        listenerRegistration = dbViewModel.listenToTimeEntriesToday { timeEntries, _ ->
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

    override fun onStop() {
        super.onStop()
        listenerRegistration.remove()
    }

    private fun setupTimer() {
        durationTimer = Timer()
        durationTimer.schedule(object : TimerTask() {
            override fun run() {
                val startTime = userViewModel.getCurrentUser().activeTimeEntry!!.startTime
                val duration = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now())
                requireActivity().runOnUiThread {
                    binding.activeTimeEntry.durationText.text = Format.timerDuration(duration)
                }
            }
        }, 0, 1000)
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

    fun goToAnalytics() {
        findNavController().navigate(R.id.action_homeFragment_to_analyticsFragment)
    }

    fun goToSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
    }
}