package com.example.trakr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.adapters.TimeEntryRecyclerViewAdapter
import com.example.trakr.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, null, false)
        binding.fragment = this
        binding.timeEntriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TimeEntryRecyclerViewAdapter(listOf())
        }
        return binding.root
    }

    fun goToNewTimeEntry() {
        requireView().findNavController().navigate(R.id.action_homeFragment_to_newTimeEntryFragment)
    }

    fun goToHistory() {
        requireView().findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
    }

    fun goToSettings() {
        requireView().findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
    }
}