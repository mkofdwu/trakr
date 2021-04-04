package com.example.trakr.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class StorageViewModel : ViewModel() {
    private val fbStorage = FirebaseStorage.getInstance()

    fun uploadPhoto(
        bitmap: Bitmap,
        name: String,
        onSuccess: OnSuccessListener<UploadTask.TaskSnapshot>,
        onFailure: OnFailureListener
    ) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val fileRef = fbStorage.reference.child("photos/$name.jpg")
        val uploadTask = fileRef.putBytes(data)
        uploadTask.addOnFailureListener(onFailure).addOnSuccessListener(onSuccess)
    }
}