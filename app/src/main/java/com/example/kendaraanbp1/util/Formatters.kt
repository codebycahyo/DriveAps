package com.example.kendaraanbp1.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun Number?.toRupiah(): String {
        if (this == null) return "Rp 0"
        val value = this.toDouble()
        if (value.isNaN() || value.isInfinite()) return "Rp -"
        
        val formatted = rupiahFormat.format(value)
        // Ensure space after Rp and remove trailing decimals if any still exist
        return formatted.replace("Rp", "Rp ").replace("Rp  ", "Rp ")
    }
}
