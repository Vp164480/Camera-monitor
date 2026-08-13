package com.example.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OfflineOcrProvider : OcrProvider {
    private val recognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

    override suspend fun processImage(bitmap: Bitmap): OcrResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            OcrResult(
                text = result.text,
                confidence = null, 
                source = "offline-ocr",
                processingInformation = "ML Kit Devanagari offline engine"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            OcrResult(
                text = "",
                confidence = null,
                source = "offline-ocr",
                processingInformation = "Failed: ${e.message}"
            )
        }
    }
}
