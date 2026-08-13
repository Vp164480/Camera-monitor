package com.example.ocr

import android.graphics.Bitmap

data class OcrResult(
    val text: String,
    val confidence: Float? = null,
    val source: String,
    val processingInformation: String = ""
)

interface OcrProvider {
    suspend fun processImage(bitmap: Bitmap): OcrResult
}
