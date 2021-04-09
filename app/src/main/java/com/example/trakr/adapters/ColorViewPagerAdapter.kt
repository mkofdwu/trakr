package com.example.trakr.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ColorViewPagerAdapter(private val colors: List<Int>) :
    RecyclerView.Adapter<ColorViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.setBackgroundColor(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}