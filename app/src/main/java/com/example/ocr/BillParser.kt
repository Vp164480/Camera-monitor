package com.example.ocr

import com.example.models.BillItem
import com.example.models.BillResult

object BillParser {

    fun parse(rawText: String, source: String = "offline-ocr"): BillResult {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val items = mutableListOf<BillItem>()
        var detectedTotal: Double? = null
        
        // Match lines like: "Product Name 2 450", "Product Name 2 450.00", "Product Name 2 ₹450", "Product Name 2 Rs450"
        // (Name) (Qty) (Currency symbol optional) (Price)
        val itemRegex = Regex("""^(.*)\s+(\d+)\s+(?:₹|Rs\.?\s*)?(\d+(?:\.\d+)?)$""", RegexOption.IGNORE_CASE)
        
        // Match total line: "Total 1480" or "Total ₹1480"
        val totalRegex = Regex("""(?i)total[\s:]+(?:₹|Rs\.?\s*)?(\d+(?:\.\d+)?)""")

        for (line in lines) {
            val itemMatch = itemRegex.find(line)
            if (itemMatch != null) {
                val (name, qtyStr, priceStr) = itemMatch.destructured
                val qty = qtyStr.toDoubleOrNull()?.toInt() ?: 1
                val price = priceStr.toDoubleOrNull() ?: 0.0
                
                // Exclude words that look like totals
                if (!name.lowercase().contains("total") && !name.lowercase().contains("subtotal")) {
                    items.add(BillItem(productName = name.trim(), quantity = qty, price = price))
                    continue
                }
            }
            
            val totalMatch = totalRegex.find(line)
            if (totalMatch != null) {
                detectedTotal = totalMatch.groupValues[1].toDoubleOrNull()
            }
        }

        val subtotal = items.sumOf { it.amount }

        return BillResult(
            items = items,
            subtotal = subtotal,
            discount = 0.0,
            detectedTotal = detectedTotal,
            source = source,
            rawText = rawText
        )
    }
}
