package com.example.trakr.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.trakr.R
import com.example.trakr.utils.Format
import java.time.LocalDateTime

class MyButton : RelativeLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate(context, R.layout.view_my_button, null)
        val args = context.obtainStyledAttributes(attrs, R.styleable.)
    }

    constructor(context: Context) : this(context, null)
}