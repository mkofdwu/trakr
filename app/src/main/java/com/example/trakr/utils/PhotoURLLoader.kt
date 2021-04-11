package com.example.trakr.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL

fun loadPhotoURLToImageView(photoURL: String, imageView: ImageView) {
    (CoroutineScope(Dispatchers.Main + Job())).launch {
        val drawable = withContext(Dispatchers.IO) {
            val stream = URL(photoURL).content as InputStream
            Drawable.createFromStream(stream, photoURL)
        }
        imageView.setImageDrawable(drawable)
    }
}