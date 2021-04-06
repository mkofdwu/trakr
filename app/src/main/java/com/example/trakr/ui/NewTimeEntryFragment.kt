package com.example.trakr.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.adapters.ColorViewPagerAdapter
import com.example.trakr.databinding.FragmentNewTimeEntryBinding
import com.example.trakr.models.TimeEntry
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel
import java.time.LocalDateTime
import kotlin.time.Duration

class NewTimeEntryFragment : Fragment() {
    private lateinit var binding: FragmentNewTimeEntryBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    private lateinit var colors: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colors = userViewModel.getCurrentUser().colors
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_time_entry, null, false)
        binding.fragment = this
        binding.colorPager.adapter = ColorViewPagerAdapter(colors)
        return binding.root
    }

    fun back() {
        requireView().findNavController().navigateUp()
    }

    fun done() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser.activeTimeEntry != null) {
            dbViewModel.addActiveTimeEntry(currentUser.activeTimeEntry!!)
        }
        currentUser.activeTimeEntry = TimeEntry(
            null,
            binding.timeEntryTitleField.text.toString(),
            LocalDateTime.now(),
            Duration.ZERO,
            colors[binding.colorPager.currentItem]
        )
        userViewModel.updateUser("activeTimeEntry", currentUser.activeTimeEntry!!.toHashMap())
        hideKeyboard()
        requireView().findNavController().navigateUp()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}