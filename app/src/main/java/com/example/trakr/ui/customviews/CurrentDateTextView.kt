package com.example.trakr.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.trakr.R
import com.example.trakr.utils.Format
import java.time.LocalDateTime

class CurrentDateTextView : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isAllCaps = true
        letterSpacing = 0.1f
        text = Format.date(LocalDateTime.now())
        textSize = 14f
        setTextColor(ContextCompat.getColor(context, R.color.text2))
    }

    constructor(context: Context) : this(context, null)
}