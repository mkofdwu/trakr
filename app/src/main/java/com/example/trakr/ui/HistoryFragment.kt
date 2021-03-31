package com.example.trakr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.trakr.R
import com.example.trakr.adapters.DayRecyclerViewAdapter
import com.example.trakr.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, null, false)
        binding.daysList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DayRecyclerViewAdapter(hashMapOf())
        }
        return binding.root
    }
}