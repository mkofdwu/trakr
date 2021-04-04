package com.example.trakr.ui

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import com.example.trakr.R
import com.example.trakr.databinding.FragmentNewTimeEntryBinding
import com.example.trakr.models.TimeEntry
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel
import java.time.LocalDateTime
import java.util.ArrayList
import kotlin.time.Duration

class NewTimeEntryFragment : Fragment() {
    private lateinit var binding: FragmentNewTimeEntryBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    private val colors = userViewModel.getCurrentUser().colors

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_time_entry, null, false)
        binding.fragment = this
        binding.colorPager.adapter =
            ViewPagerAdapter(colors, requireActivity().supportFragmentManager)
        return binding.root
    }

    fun done() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser.activeTimeEntry != null) {
            dbViewModel.addTimeEntry(currentUser.activeTimeEntry!!)
        }
        currentUser.activeTimeEntry = TimeEntry(
            null,
            binding.timeEntryTitleField.text.toString(),
            LocalDateTime.now(),
            Duration.ZERO,
            colors[binding.colorPager.currentItem]
        )
    }

    internal class ViewPagerAdapter(
        private val colors: List<Int>,
        supportFragmentManager: FragmentManager?
    ) :
        FragmentPagerAdapter(supportFragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(i: Int): Fragment {
            return object : Fragment() {
                override fun onCreateView(
                    inflater: LayoutInflater,
                    container: ViewGroup?,
                    savedInstanceState: Bundle?
                ): View {
                    val view = View(context)
                    view.setBackgroundColor(colors[i])
                    return view
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}