package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.example.ocr.OcrProvider

object PdfProcessor {
    suspend fun processPdfWithOcr(context: Context, uri: Uri, ocrProvider: OcrProvider, onProgress: (Int, Int) -> Unit): String = withContext(Dispatchers.IO) {
        val allText = StringBuilder()
        var fileDescriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            val tempFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(fileDescriptor)
            
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                withContext(Dispatchers.Main) {
                    onProgress(i + 1, pageCount)
                }
                val page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap(
                    page.width * 2,
                    page.height * 2,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(android.graphics.Color.WHITE)
                
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                val text = ocrProvider.processImage(bitmap)
                allText.append(text).append("\n")
                bitmap.recycle()
                page.close()
            }
            tempFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            renderer?.close()
            fileDescriptor?.close()
        }
        return@withContext allText.toString()
    }
}
