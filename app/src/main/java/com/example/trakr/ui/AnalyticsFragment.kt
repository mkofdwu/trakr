package com.example.trakr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.trakr.R
import com.example.trakr.databinding.FragmentAnalyticsBinding

class AnalyticsFragment : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analytics, null, false)
        return binding.root
    }

    fun back() {

    }

    fun goToHistory() {}
}