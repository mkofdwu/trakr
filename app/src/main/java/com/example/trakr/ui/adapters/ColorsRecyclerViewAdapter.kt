package com.example.trakr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trakr.R
import com.example.trakr.ui.fragments.ColorsSettingsFragment
import com.google.android.material.card.MaterialCardView

class ColorsRecyclerViewAdapter(
    private var colors: List<Int>,
    private val fragment: ColorsSettingsFragment
) :
    RecyclerView.Adapter<ColorsRecyclerViewAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        if (position < colors.size) return ITEM_COLOR
        return ITEM_ADD_BTN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_COLOR -> {
                val view = inflater.inflate(R.layout.view_color, parent, false) as MaterialCardView
                ViewHolder(view)
            }
            ITEM_ADD_BTN -> {
                val view =
                    inflater.inflate(R.layout.view_add_color_btn, parent, false) as MaterialCardView
                ViewHolder(view)
            }
            else -> throw Error("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < colors.size) {
            holder.view.setCardBackgroundColor(colors[position])
            holder.view.setOnLongClickListener { fragment.removeColor(position); true }
        } else {
            holder.view.setOnClickListener { fragment.addColor() }
        }
    }

    override fun getItemCount(): Int = colors.size + 1 // for add button

    inner class ViewHolder(val view: MaterialCardView) : RecyclerView.ViewHolder(view) {}

    companion object {
        const val ITEM_COLOR = 0
        const val ITEM_ADD_BTN = 1
    }
}