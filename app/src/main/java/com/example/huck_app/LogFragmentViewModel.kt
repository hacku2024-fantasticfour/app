package com.example.huck_app.com.example.huck_app

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogFragmentViewModel : ViewModel() {
    private val _detectedImage = MutableLiveData<Bitmap>()
    val detectedImage: LiveData<Bitmap> get() = _detectedImage

    fun setDetectedImage(bitmap: Bitmap) {
        _detectedImage.value = bitmap
    }
}
