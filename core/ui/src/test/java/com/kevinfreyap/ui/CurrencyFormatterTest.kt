package com.kevinfreyap.ui

import com.kevinfreyap.ui.util.formatStockDisplay
import com.kevinfreyap.ui.util.parseCurrencyAmount
import com.kevinfreyap.ui.util.toFormattedCurrency
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testPhilippinePesoCurrencyFormatting() {
        assertEquals("₱0.00", (0.0).toFormattedCurrency())
        assertEquals("₱1.00", (1.0).toFormattedCurrency())
        assertEquals("₱150.00", (150.0).toFormattedCurrency())
        assertEquals("₱1,250.50", (1250.50).toFormattedCurrency())
        assertEquals("₱125,000.75", (125000.75).toFormattedCurrency())
    }

    @Test
    fun testCurrencyParsing() {
        assertEquals(0.0, "₱0.00".parseCurrencyAmount(), 0.001)
        assertEquals(1.0, "₱1.00".parseCurrencyAmount(), 0.001)
        assertEquals(150.0, "₱150.00".parseCurrencyAmount(), 0.001)
        assertEquals(1250.50, "₱1,250.50".parseCurrencyAmount(), 0.001)
        assertEquals(125000.75, "₱125,000.75".parseCurrencyAmount(), 0.001)
    }

    @Test
    fun testStockDisplayFormatting() {
        assertEquals("24 pcs", formatStockDisplay(24, "pcs"))
        assertEquals("0 pcs", formatStockDisplay(0, ""))
        assertEquals("5 L", formatStockDisplay(5, "L"))
    }
}
