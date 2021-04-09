package com.example.trakr.ui.misc

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class UnscrollableGridLayoutManager(context: Context, spanCount: Int) :
    GridLayoutManager(context, spanCount) {
    override fun canScrollVertically(): Boolean {
        return false
    }
}