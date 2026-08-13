package com.example.ocr

import android.graphics.Bitmap
import kotlinx.coroutines.delay

class OnlineOcrProvider(
    private val endpoint: String,
    private val apiKey: String
) : OcrProvider {
    override suspend fun processImage(bitmap: Bitmap): OcrResult {
        // Simulate network delay
        delay(1500)
        
        // Mock result for prototype purposes
        return OcrResult(
            text = "Sample Online OCR Result\nSpark Plug 4 80\nTotal 320",
            confidence = 0.98f,
            source = "online-ocr",
            processingInformation = "Processed via $endpoint"
        )
    }
}
