package com.example.trakr.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrentDateTextView : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM"))
    }

    constructor(context: Context) : this(context, null)

}