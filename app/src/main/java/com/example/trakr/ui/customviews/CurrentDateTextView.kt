package com.example.trakr.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.trakr.utils.Format
import java.time.LocalDateTime

class CurrentDateTextView : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        text = Format.date(LocalDateTime.now())
    }

    constructor(context: Context) : this(context, null)

}