package com.example.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PreferencesManager
import com.example.models.BillItem
import com.example.models.BillResult
import com.example.ocr.BillParser
import com.example.ocr.OcrProvider
import com.example.ocr.OfflineOcrProvider
import com.example.ocr.OnlineOcrProvider
import com.example.pdf.PdfProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScannerState {
    object Idle : ScannerState()
    data class ConfirmOnlineUpload(val uri: Uri, val isPdf: Boolean) : ScannerState()
    data class Processing(val progressText: String) : ScannerState()
    data class Review(val billResult: BillResult, val originalUris: List<Uri> = emptyList()) : ScannerState()
    data class Error(val message: String) : ScannerState()
}

class ScannerViewModel : ViewModel() {
    private val _state = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val state = _state.asStateFlow()
    
    private val offlineOcrProvider = OfflineOcrProvider()
    private val onlineOcrProvider = OnlineOcrProvider("https://api.example.com/ocr", "")
    
    private var currentBillResult: BillResult? = null

    fun reset() {
        _state.value = ScannerState.Idle
        currentBillResult = null
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun handleIncomingDocument(context: Context, uri: Uri, isPdf: Boolean) {
        val mode = PreferencesManager.getOcrMode(context)
        val hasNet = isNetworkAvailable(context)
        
        when (mode) {
            "Offline Only" -> {
                executeProcessing(context, uri, isPdf, offlineOcrProvider)
            }
            "Online OCR" -> {
                _state.value = ScannerState.ConfirmOnlineUpload(uri, isPdf)
            }
            "Automatic" -> {
                if (hasNet) {
                    _state.value = ScannerState.ConfirmOnlineUpload(uri, isPdf)
                } else {
                    executeProcessing(context, uri, isPdf, offlineOcrProvider)
                }
            }
            else -> executeProcessing(context, uri, isPdf, offlineOcrProvider)
        }
    }

    fun confirmOnlineUpload(context: Context, uri: Uri, isPdf: Boolean, approved: Boolean) {
        if (approved) {
            executeProcessing(context, uri, isPdf, onlineOcrProvider)
        } else {
            // Fallback to offline if they cancel online
            executeProcessing(context, uri, isPdf, offlineOcrProvider)
        }
    }

    private fun executeProcessing(context: Context, uri: Uri, isPdf: Boolean, ocrProvider: OcrProvider) {
        if (isPdf) {
            processPdf(context, uri, ocrProvider)
        } else {
            processImage(context, uri, ocrProvider)
        }
    }

    private fun processImage(context: Context, uri: Uri, ocrProvider: OcrProvider) {
        viewModelScope.launch {
            _state.value = ScannerState.Processing("Processing image...")
            try {
                val bitmap = uriToBitmap(context, uri)
                if (bitmap != null) {
                    val ocrResult = ocrProvider.processImage(bitmap)
                    if (ocrResult.text.isBlank()) {
                        _state.value = ScannerState.Error("No readable text detected. Try a clearer photo.")
                        return@launch
                    }
                    val result = BillParser.parse(ocrResult.text, ocrResult.source)
                    currentBillResult = result
                    _state.value = ScannerState.Review(result, listOf(uri))
                } else {
                    _state.value = ScannerState.Error("Failed to load image.")
                }
            } catch (e: Exception) {
                _state.value = ScannerState.Error("OCR failed: ${e.message}")
            }
        }
    }

    private fun processPdf(context: Context, uri: Uri, ocrProvider: OcrProvider) {
        viewModelScope.launch {
            try {
                _state.value = ScannerState.Processing("Loading PDF...")
                val ocrResult = PdfProcessor.processPdfWithOcr(context, uri, ocrProvider) { current, total ->
                    _state.value = ScannerState.Processing("Processing page $current of $total...")
                }
                
                if (ocrResult.text.isBlank()) {
                    _state.value = ScannerState.Error("Could not read PDF or PDF is empty.")
                    return@launch
                }
                
                val result = BillParser.parse(ocrResult.text, ocrResult.source)
                currentBillResult = result
                _state.value = ScannerState.Review(result, listOf(uri))
            } catch (e: Exception) {
                _state.value = ScannerState.Error("PDF Processing failed: ${e.message}")
            }
        }
    }
    
    fun processDemoBill() {
        viewModelScope.launch {
            _state.value = ScannerState.Processing("Loading Demo Bill...")
            val demoText = """
                Mahadev Auto Garage
                इंजन ऑयल 2 450
                Brake Shoe 1 ₹280
                एयर फिल्टर 2 Rs 150
                Spark Plug 4 80
                Total ₹1800
            """.trimIndent()
            
            val result = BillParser.parse(demoText, "demo")
            currentBillResult = result
            _state.value = ScannerState.Review(result, emptyList())
        }
    }

    fun updateItem(updatedItem: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.map { if (it.id == updatedItem.id) updatedItem else it }
        val newResult = current.copy(items = newItems)
        currentBillResult = newResult
        _state.value = ScannerState.Review(newResult)
    }

    fun deleteItem(item: BillItem) {
        val current = currentBillResult ?: return
        val newItems = current.items.filter { it.id != item.id }
        val newResult = current.copy(items = newItems)
        currentBillResult = newResult
        _state.value = ScannerState.Review(newResult)
    }

    fun addItem() {
        val current = currentBillResult ?: return
        val newItems = current.items.toMutableList()
        newItems.add(BillItem(productName = "New Item", quantity = 1, price = 0.0))
        val newResult = current.copy(items = newItems)
        currentBillResult = newResult
        _state.value = ScannerState.Review(newResult)
    }

    fun updateDiscount(discount: Double) {
        val current = currentBillResult ?: return
        val newResult = current.copy(discount = discount)
        currentBillResult = newResult
        _state.value = ScannerState.Review(newResult)
    }
    
    @Suppress("DEPRECATION")
    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
