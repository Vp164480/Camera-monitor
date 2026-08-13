package com.example.ocr

import org.junit.Assert.assertEquals
import org.junit.Test

class BillParserTest {

    @Test
    fun `parse standard printed bill`() {
        val rawText = """
            Mahadev Auto Garage
            Engine Oil 2 450
            Brake Shoe 1 280
            Air Filter 2 150
            Total 1480
        """.trimIndent()

        val result = BillParser.parse(rawText)

        assertEquals(3, result.items.size)
        
        assertEquals("Engine Oil", result.items[0].productName)
        assertEquals(2, result.items[0].quantity)
        assertEquals(450.0, result.items[0].price, 0.01)
        
        assertEquals(1480.0, result.subtotal, 0.01)
        assertEquals(1480.0, result.detectedTotal)
    }

    @Test
    fun `parse bill with currency symbols`() {
        val rawText = """
            Brake shoe 1 ₹280
            Hero oil 2 Rs450
            Total ₹1480
        """.trimIndent()

        val result = BillParser.parse(rawText)

        assertEquals(2, result.items.size)
        assertEquals("Brake shoe", result.items[0].productName)
        assertEquals(280.0, result.items[0].price, 0.01)
        
        assertEquals("Hero oil", result.items[1].productName)
        assertEquals(450.0, result.items[1].price, 0.01)
        
        assertEquals(1180.0, result.subtotal, 0.01)
        assertEquals(1480.0, result.detectedTotal) // intentionally mismatched subtotal to test extraction
    }
}
