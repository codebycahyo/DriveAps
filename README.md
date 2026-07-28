# DriveTrack

Aplikasi pencatatan kendaraan pribadi **offline** untuk mencatat pengisian BBM, riwayat servis, dokumen kendaraan, dan pengingat masa berlaku — lengkap dengan autentikasi akun lokal, mode terang/gelap, OCR struk, ekspor laporan, backup database, dan notifikasi lokal.

> Tugas Final Projek — dibangun dengan **Kotlin** dan **XML layout** (Android Views).

---

## Fitur

| Modul | Highlight |
|---|---|
| Autentikasi | Daftar & masuk dengan **akun lokal** (offline): email unik, kata sandi di-hash, sesi tersimpan (auto-login), logout |
| Kendaraan | Tambah/edit/detail, multi-kendaraan, jenis (Mobil/Motor), pilih tahun, foto kendaraan |
| BBM | Catat pengisian, **Total Biaya otomatis** (liter × harga), ringkasan konsumsi & efisiensi km/L |
| Servis | Riwayat per kategori (oli, ban, tune-up, dll), reminder servis berikutnya |
| Dokumen | STNK / BPKB / SIM / Pajak / Asuransi dengan masa berlaku |
| Scan Nota | OCR via Google ML Kit, parse total/tanggal/bengkel otomatis ke form |
| Notifikasi | Pengingat lokal H-7, H-1, H-0 untuk servis & masa berlaku dokumen |
| Backup | Salinan database `.db` lokal, restore, kirim via share sheet |
| Ekspor | Laporan PDF & CSV untuk arsip pribadi |
| Tema | **Light / Dark / Ikuti Sistem** — dirancang penuh (Material 3), bukan sekadar warna dibalik |
| Profil | Tema, bahasa, keamanan, bantuan, T&C, privasi |

---

## Tech Stack

- **Kotlin** + Android Views (**XML layout**, ConstraintLayout)
- **Architecture**: MVVM (ViewModel + StateFlow/Flow)
- **DB**: Room (SQLite) + SharedPreferences
- **Navigation**: Navigation Component (Navigation Graph + Safe Args)
- **Binding**: ViewBinding
- **Ekspor**: iText / PdfDocument API (PDF), OpenCSV (CSV)
- **Foto**: Glide (loading & caching)
- **OCR**: Google ML Kit Text Recognition
- **Kamera**: CameraX
- **Notifikasi**: WorkManager / AlarmManager
- **Keamanan kata sandi**: salted, iterated SHA-256 (`java.security`, tanpa library eksternal)
- **Font**: Plus Jakarta Sans (via `res/font/`)

---

## Arsitektur

Pendekatan **2-layer simpel**: data → UI.

```
app/
├── data/
│   ├── local/
│   │   ├── entity/        # Room Entity (Vehicle, FuelLog, ServiceLog, VehicleDocument, User)
│   │   ├── dao/            # Room DAO per tabel
│   │   └── AppDatabase.kt # Room singleton
│   ├── repository/         # query logic + AuthRepository, return Result<T> / sealed Resource
│   └── model/               # data class (UI model)
├── logic/                   # logika domain murni (OOP) — mis. Pengeluaran (abstract + subclass)
├── ui/
│   ├── login/  register/    # autentikasi akun lokal
│   ├── onboarding/  home/  vehicle*/  fuel*/  service*/  documents/
│   ├── scan/  profile/
│   └── common/
│       ├── adapter/         # RecyclerView Adapter + DiffUtil
│       └── viewmodel/        # ViewModel + ViewModelFactory (DI manual)
├── util/             # Formatters, PasswordHasher, SessionManager, Validators, ExportHelper, VehicleStats
└── service/          # NotificationScheduler (WorkManager/AlarmManager)
```

**Konvensi**:
- DI manual via constructor + `ViewModelFactory` (NO Hilt/Koin).
- MVVM: UI Event → ViewModel → StateFlow/Flow → Fragment observe.
- Repository return `Result<T>` / sealed class `Resource` — error & data terpisah jelas.
- Foto disimpan di app-specific directory, di-compress 1280px / 80% sebelum simpan.
- RecyclerView wajib pakai `DiffUtil`.
- ViewBinding di setiap Fragment/Activity.

---

## Autentikasi (Offline)

Aplikasi memakai **akun lokal** yang tersimpan di perangkat (tanpa server):

- **Registrasi** menyimpan akun ke tabel `users` (email unik, kata sandi disimpan sebagai *hash* ber-salt, bukan teks polos).
- **Login** memverifikasi email + kata sandi terhadap akun tersimpan. Email belum terdaftar atau kata sandi salah ditolak dengan pesan yang jelas.
- **Sesi** disimpan via `SessionManager` (SharedPreferences) → **auto-login** saat aplikasi dibuka kembali; **Logout** di Profil menghapus sesi.
- Validasi form penuh (email, panjang kata sandi, konfirmasi, persetujuan syarat) + proteksi tap ganda.

---

## OOP Domain

Logika inti dipisah ke lapisan `logic/` (semangat MVC ringan). Contoh utama: hierarki **`Pengeluaran`** yang menghitung **Total Pengeluaran** di dashboard secara polimorfisme:

- `abstract class Pengeluaran` — kerangka (abstraction).
- `PengeluaranBBM`, `PengeluaranServis` — subclass (inheritance) dengan `hitungTotal()` berbeda (polymorphism); `PengeluaranBBM` menjaga `liter` lewat setter bervalidasi (encapsulation).

---

## Database

Tabel utama:

| Tabel | Isi |
|---|---|
| `users` | Akun lokal (email unik, hash kata sandi) untuk login offline |
| `vehicles` | Master kendaraan, multi-kendaraan |
| `fuel_logs` | Catatan pengisian BBM, link `vehicle_id` |
| `service_logs` | Catatan servis + `next_service_date` untuk reminder |
| `vehicle_documents` | STNK/Pajak/dll + `expiry_date` untuk reminder |

DB dikelola via Room, singleton `AppDatabase`, disimpan di internal storage app.

---

## Logic Notifikasi Lokal

### Kapan dijadwalkan
Tiga slot otomatis dijadwalkan setiap kali user **menyimpan / mengubah** record yang punya tanggal jatuh tempo:
- **H-7** (7 hari sebelum), **H-1** (sehari sebelum), **H-0** (hari H, pagi)

Semuanya fire **jam 09:00 waktu lokal device**. Slot yang sudah lewat saat scheduling di-skip otomatis.

### ID namespacing (anti-collision)
```
service log     : 10_000_000 + (serviceId * 10) + dayCode
vehicle document: 20_000_000 + (documentId * 10) + dayCode
```
`dayCode` ∈ `{0, 1, 7}`.

`BroadcastReceiver` boot (`RECEIVE_BOOT_COMPLETED`) didaftarkan agar antrian notif survive restart device.

---

## Logic Backup & Restore

| Aksi | Yang terjadi |
|---|---|
| **Backup** | Copy database Room → `getExternalFilesDir(null)/drivetrack_backup_<timestamp>.db` |
| **Restore** | Copy file backup terpilih → menimpa database aktif. Snackbar minta user restart app |
| **Kirim** | `Intent.ACTION_SEND` + `FileProvider` → share sheet (WhatsApp/Drive/Gmail/Telegram) |
| **Hapus** | `File.delete()` lalu refresh list |

Catatan: backup hanya berisi file database mentah. Foto pendukung (struk, foto kendaraan) **belum** ter-include.

---

## Setup

```bash
# Buka project di Android Studio → Sync Gradle
./gradlew build
# Jalankan:
./gradlew installDebug
```

---

## Permission Android

Didaftarkan di `app/src/main/AndroidManifest.xml`:

| Permission | Untuk |
|---|---|
| `CAMERA` | Foto kendaraan & scan nota |
| `READ_MEDIA_IMAGES` (Android 13+) | Pick foto dari galeri |
| `READ_EXTERNAL_STORAGE` (≤ Android 12) | Legacy image picker |
| `POST_NOTIFICATIONS` (Android 13+) | Notifikasi lokal |
| `RECEIVE_BOOT_COMPLETED` | Reschedule notif setelah reboot |
| `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM` | Reminder presisi via AlarmManager |

---

## Navigation

Pakai **Navigation Component**:
- `MainActivity` + `NavHostFragment`; start destination ditentukan runtime (auto-login → Home, jika belum → Onboarding/Login).
- Bottom navigation (Home, Kendaraan, Laporan, Profil) tersedia di layar utama.
- Navigasi antar Fragment via `NavController` + Safe Args.
- Shared ViewModel (scope ke `Activity`) untuk data lintas Fragment.

---

## Convention

- Bahasa UI: **Indonesia**.
- Currency: `Formatters.currency(value)` → `Rp 1.250.000`.
- Date: `Formatters.date(date)` → `'dd MMMM yyyy'`, locale `id_ID`.
- Tema: token warna di `res/values/colors.xml` + `res/values-night/colors.xml` (Material 3, light & dark).

---

## Dokumentasi tambahan

- **PRD**: `docs/prd/KendaraanKu_PRD_v1.1.txt`
- **Design**: `docs/design/` (referensi visual "Luminous Frosted")
- **Rules & dev plan**: `.claude/rules.md`, `.claude/development-plan.md`
