package com.example.kendaraanbp1.logic

/**
 * ============================================================================
 *  HIERARKI OOP DOMAIN — "Pengeluaran Kendaraan"
 *  Kelas-kelas ini adalah lapisan LOGIKA murni (MVC ringan): tidak menyentuh
 *  Android, database, atau UI. Dipakai nyata oleh VehicleStats.totalPengeluaran()
 *  untuk menghitung angka "Total Pengeluaran" di dashboard.
 *
 *  Empat konsep OOP yang dinilai terlihat di sini:
 *   1. ABSTRACTION  -> abstract class Pengeluaran sebagai kerangka.
 *   2. INHERITANCE  -> PengeluaranBBM & PengeluaranServis mewarisi Pengeluaran.
 *   3. POLYMORPHISM -> hitungTotal() di-override, hasilnya beda tiap subclass,
 *                      dipanggil lewat tipe induk (val p: Pengeluaran = ...).
 *   4. ENCAPSULATION-> property private `liter` dilindungi setter bervalidasi.
 * ============================================================================
 */

// 1. ABSTRACTION: kerangka umum untuk semua jenis pengeluaran.
abstract class Pengeluaran(val tanggal: Long) {

    // Wajib diisi tiap subclass (perilaku berbeda-beda -> polimorfisme).
    abstract fun kategori(): String
    abstract fun hitungTotal(): Double

    // Method konkrit yang dipakai bersama oleh semua subclass.
    fun ringkasan(): String {
        return "${kategori()}: Rp ${hitungTotal().toLong()}"
    }
}

// 2. INHERITANCE + 3. POLYMORPHISM + 4. ENCAPSULATION
// Pengeluaran BBM: totalnya dihitung dari liter x harga per liter.
class PengeluaranBBM(
    tanggal: Long,
    literAwal: Double,
    private val hargaPerLiter: Double,
) : Pengeluaran(tanggal) {

    // ENKAPSULASI: `liter` private, hanya bisa diubah lewat setLiter() yang memvalidasi.
    private var liter: Double = 0.0

    init {
        setLiter(literAwal)
    }

    fun setLiter(nilai: Double) {
        if (nilai >= 0.0) {          // validasi: liter tidak boleh negatif
            liter = nilai
        }
    }

    fun getLiter(): Double = liter

    override fun kategori(): String = "BBM"

    // POLIMORFISME: rumus khas BBM.
    override fun hitungTotal(): Double = getLiter() * hargaPerLiter
}

// Pengeluaran Servis: totalnya langsung dari biaya jasa/sparepart.
class PengeluaranServis(
    tanggal: Long,
    val namaLayanan: String,
    private val biaya: Double,
) : Pengeluaran(tanggal) {

    override fun kategori(): String = "Servis"

    // POLIMORFISME: rumus khas servis (beda dengan BBM).
    override fun hitungTotal(): Double = biaya
}
