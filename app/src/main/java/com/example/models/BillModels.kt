package com.example.models

import java.util.UUID

data class BillItem(
    val id: String = UUID.randomUUID().toString(),
    var productName: String,
    var quantity: Int,
    var price: Double,
    var confidence: Float? = null
) {
    val amount: Double get() = quantity * price
}

data class BillResult(
    val items: List<BillItem>,
    val subtotal: Double,
    var discount: Double = 0.0,
    val detectedTotal: Double? = null,
    val source: String = "offline-ocr",
    val rawText: String = ""
) {
    val calculatedTotal: Double get() = items.sumOf { it.amount } - discount
}
