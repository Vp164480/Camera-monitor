package com.example.ocr

import android.graphics.Bitmap

interface OcrProvider {
    suspend fun processImage(bitmap: Bitmap): String
}
