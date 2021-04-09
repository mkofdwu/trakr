package com.example.trakr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.adapters.ColorViewPagerAdapter
import com.example.trakr.databinding.FragmentEditTimeEntryBinding
import com.example.trakr.models.TimeEntry
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel

class EditTimeEntryFragment : Fragment() {
    private lateinit var binding: FragmentEditTimeEntryBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    private lateinit var timeEntry: TimeEntry
    private lateinit var colors: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeEntry = requireArguments().getSerializable("timeEntry") as TimeEntry
        colors = userViewModel.getCurrentUser().colors
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_time_entry, null, false)
        binding.fragment = this
        binding.timeEntry = timeEntry
        binding.colorPager.adapter = ColorViewPagerAdapter(colors)
        binding.colorPager.currentItem = colors.indexOf(timeEntry.color)
        return binding.root
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun deleteTimeEntry() {
        dbViewModel.deleteTimeEntry(timeEntry.id!!)
        findNavController().navigateUp()
    }

    fun done() {
        timeEntry.title = binding.timeEntryTitleField.text.toString()
        timeEntry.color = colors[binding.colorPager.currentItem]
        dbViewModel.updateTimeEntry(timeEntry)
        findNavController().navigateUp()
    }
}