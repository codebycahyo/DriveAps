package com.example.kendaraanbp1.util

import android.util.Log

data class FuelReceiptData(
    val total: Double?,
    val dateStr: String?,
    val stationName: String?
)

object ReceiptParser {
    private const val TAG = "ReceiptParser"

    fun parseFuelReceipt(rawText: String): FuelReceiptData {
        Log.d(TAG, "Raw OCR Text: \n$rawText")
        
        val lines = rawText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        var total: Double? = null
        var dateStr: String? = null
        var stationName: String? = null

        // Basic Regex for date: dd/mm/yyyy or dd-mm-yyyy or dd/mm/yy
        val dateRegex = Regex("""(\d{2})[-/](\d{2})[-/](\d{2,4})""")

        // Common SPBU keywords
        val stationKeywords = listOf("SPBU", "PERTAMINA", "SHELL", "BP", "VIVO")

        for (line in lines) {
            val upperLine = line.uppercase()

            // 1. Try to find Station Name
            if (stationName == null) {
                if (stationKeywords.any { upperLine.contains(it) }) {
                    stationName = line
                }
            }

            // 2. Try to find Date
            if (dateStr == null) {
                val match = dateRegex.find(line)
                if (match != null) {
                    dateStr = match.value
                }
            }

            // 3. Try to find Total Amount
            // Often receipts have "TOTAL Rp 100.000" or similar. 
            // We look for "TOTAL" or "TOTAL BAYAR" and extract the largest number.
            if (total == null) {
                if (upperLine.contains("TOTAL") || upperLine.contains("RP") || upperLine.contains("BAYAR")) {
                    // Extract all numbers in this line, potentially with dots or commas
                    val numberRegex = Regex("""\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?""")
                    val matches = numberRegex.findAll(line).toList()
                    if (matches.isNotEmpty()) {
                        // Take the last match on the line (usually the total)
                        val lastMatch = matches.last().value
                        val parsedVal = parseLocalizedNumber(lastMatch)
                        if (parsedVal != null && parsedVal > 1000) {
                            total = parsedVal
                        } else {
                            Log.e(TAG, "Failed to parse total: $lastMatch")
                        }
                    }
                }
            }
        }

        return FuelReceiptData(total, dateStr, stationName)
    }

    /**
     * Smartly parses a localized number string into a Double.
     *
     * Handles two common formats:
     * - **Indonesian**: `350.000,00` (dot = thousands separator, comma = decimal) → 350000.0
     * - **English**:    `350,000.00` (comma = thousands separator, dot = decimal)  → 350000.0
     * - **Plain**:      `350000` → 350000.0
     *
     * The detection heuristic: if the string contains a comma AND the comma comes
     * after a dot, the format is Indonesian (dot=thousands, comma=decimal).
     * If the string contains a dot AND the dot comes after a comma, the format is
     * English (comma=thousands, dot=decimal).
     */
    private fun parseLocalizedNumber(raw: String): Double? {
        return try {
            val lastDot = raw.lastIndexOf('.')
            val lastComma = raw.lastIndexOf(',')

            val normalized = when {
                // Indonesian format: "350.000,00" — comma is after dot → comma is decimal separator
                lastComma > lastDot -> raw.replace(".", "").replace(",", ".")
                // English format: "350,000.00" — dot is after comma → comma is thousands separator
                lastDot > lastComma -> raw.replace(",", "")
                // No separator or same position: plain integer-like
                else -> raw
            }
            normalized.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
