package com.example.kendaraanbp1.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportHelper {

    private const val AUTHORITY_SUFFIX = ".fileprovider"
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val X_MARGIN = 50f
    private const val Y_START = 50f
    private const val Y_BOTTOM_MARGIN = 800f
    private const val LINE_HEIGHT = 20f

    /**
     * Sanitizes a string to be safe for use as a filename by replacing
     * any character that is not alphanumeric, space, or hyphen with underscore.
     */
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9 \\-]"), "_").replace(" ", "_")
    }

    fun exportToCsv(
        context: Context,
        vehicle: VehicleEntity,
        fuelList: List<FuelLogEntity>,
        serviceList: List<ServiceLogEntity>
    ): Uri? {
        try {
            val vehicleName = "${vehicle.brand} ${vehicle.model}"
            val safeVehicleName = sanitizeFileName(vehicleName)
            val fileName = "Report_${safeVehicleName}_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            
            val sb = StringBuilder()
            
            // Header
            sb.append("Laporan Kendaraan: $vehicleName (${vehicle.plateNumber})\n\n")
            
            // Fuel Section
            sb.append("Riwayat Pengisian BBM\n")
            sb.append("Tanggal,Liter,Harga per Liter,Total Harga,Odometer,SPBU\n")
            for (f in fuelList) {
                val total = f.liters * f.pricePerLiter
                sb.append("${formatDate(f.date)},${f.liters},${f.pricePerLiter},$total,${f.odometer},\"${f.stationName}\"\n")
            }
            sb.append("\n")
            
            // Service Section
            sb.append("Riwayat Servis\n")
            sb.append("Tanggal,Tipe Servis,Biaya,Odometer,Bengkel\n")
            for (s in serviceList) {
                sb.append("${formatDate(s.date)},\"${s.category}\",${s.totalCost},${s.odometer},\"${s.workshopName}\"\n")
            }
            
            FileOutputStream(file).use {
                it.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            
            return FileProvider.getUriForFile(context, context.packageName + AUTHORITY_SUFFIX, file)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportToPdf(
        context: Context,
        vehicle: VehicleEntity,
        fuelList: List<FuelLogEntity>,
        serviceList: List<ServiceLogEntity>
    ): Uri? {
        try {
            val document = PdfDocument()
            val vehicleName = "${vehicle.brand} ${vehicle.model}"

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 18f
                isFakeBoldText = true
            }
            val subtitlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                isFakeBoldText = true
            }

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas: Canvas = page.canvas
            var yPos = Y_START

            // Helper lambda: start a new page when near the bottom
            fun checkNewPage() {
                if (yPos > Y_BOTTOM_MARGIN) {
                    document.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = Y_START
                }
            }

            // Title (only on first page)
            canvas.drawText("Laporan Kendaraan: $vehicleName (${vehicle.plateNumber})", X_MARGIN, yPos, titlePaint)
            yPos += 30f

            // ─── FUEL SECTION ───────────────────────────────────────────
            checkNewPage()
            canvas.drawText("Riwayat Pengisian BBM", X_MARGIN, yPos, subtitlePaint)
            yPos += LINE_HEIGHT

            paint.isFakeBoldText = true
            checkNewPage()
            canvas.drawText("Tanggal | Liter | Harga/L | Total | Odometer | SPBU", X_MARGIN, yPos, paint)
            yPos += LINE_HEIGHT
            paint.isFakeBoldText = false

            if (fuelList.isEmpty()) {
                checkNewPage()
                canvas.drawText("(Tidak ada data BBM)", X_MARGIN, yPos, paint)
                yPos += LINE_HEIGHT
            } else {
                for (f in fuelList) {
                    checkNewPage()
                    val total = f.liters * f.pricePerLiter
                    val text = "${formatDate(f.date)} | ${f.liters}L | Rp${f.pricePerLiter} | Rp$total | ${f.odometer}km | ${f.stationName}"
                    canvas.drawText(text, X_MARGIN, yPos, paint)
                    yPos += LINE_HEIGHT
                }
            }

            yPos += LINE_HEIGHT

            // ─── SERVICE SECTION ─────────────────────────────────────────
            checkNewPage()
            canvas.drawText("Riwayat Servis", X_MARGIN, yPos, subtitlePaint)
            yPos += LINE_HEIGHT

            paint.isFakeBoldText = true
            checkNewPage()
            canvas.drawText("Tanggal | Tipe | Biaya | Odometer | Bengkel", X_MARGIN, yPos, paint)
            yPos += LINE_HEIGHT
            paint.isFakeBoldText = false

            if (serviceList.isEmpty()) {
                checkNewPage()
                canvas.drawText("(Tidak ada data servis)", X_MARGIN, yPos, paint)
                yPos += LINE_HEIGHT
            } else {
                for (s in serviceList) {
                    checkNewPage()
                    val text = "${formatDate(s.date)} | ${s.category} | Rp${s.totalCost} | ${s.odometer}km | ${s.workshopName}"
                    canvas.drawText(text, X_MARGIN, yPos, paint)
                    yPos += LINE_HEIGHT
                }
            }

            document.finishPage(page)

            val safeVehicleName = sanitizeFileName(vehicleName)
            val fileName = "Report_${safeVehicleName}_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            
            FileOutputStream(file).use {
                document.writeTo(it)
            }
            
            document.close()
            
            return FileProvider.getUriForFile(context, context.packageName + AUTHORITY_SUFFIX, file)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
