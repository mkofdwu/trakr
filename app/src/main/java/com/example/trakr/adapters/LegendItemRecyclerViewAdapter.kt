package com.example.trakr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.databinding.ViewLegendItemBinding
import com.example.trakr.models.LegendItem

class LegendItemRecyclerViewAdapter(private val legendItems: List<LegendItem>) :
    RecyclerView.Adapter<LegendItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewLegendItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_legend_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val legendItem = legendItems[position]
        holder.binding.legendItem = legendItem
    }

    override fun getItemCount(): Int = legendItems.size

    inner class ViewHolder(val binding: ViewLegendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}