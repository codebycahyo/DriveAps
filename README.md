    # KendaraanKu

    Aplikasi pencatatan kendaraan pribadi **100% offline** untuk mencatat pengisian BBM, riwayat servis, dokumen kendaraan, dan pengingat masa berlaku — lengkap dengan OCR struk, ekspor laporan, backup database, dan notifikasi lokal.

    > Tugas Final Projek — dibangun dengan **Kotlin** dan **XML layout** (Android Views).

    ---

    ## Fitur

    | Modul | Highlight |
    |---|---|
    | Kendaraan | Tambah/edit/detail, multi-kendaraan per user, foto kendaraan |
    | BBM | Catat pengisian, harga/liter otomatis, ringkasan konsumsi & total biaya |
    | Servis | Riwayat per kategori (oli, ban, tune-up, dll), reminder servis berikutnya |
    | Dokumen | STNK / BPKB / SIM / Pajak / Asuransi dengan masa berlaku |
    | Scan Nota | OCR via Google ML Kit, parse total/tanggal/bengkel otomatis ke form |
    | Notifikasi | Pengingat lokal H-7, H-1, H-0 untuk servis & masa berlaku dokumen |
    | Backup | Salinan database `.db` lokal, restore, kirim via share sheet |
    | Ekspor | Laporan PDF & CSV untuk arsip pribadi |
    | Profil | Tema, bahasa, keamanan, bantuan, T&C, privasi |

    ---

    ## Tech Stack

    - **Kotlin** + Android Views (**XML layout**, ConstraintLayout)
    - **Architecture**: MVVM (ViewModel + LiveData/StateFlow)
    - **DB**: Room (SQLite) + SharedPreferences/DataStore
    - **Navigation**: Navigation Component (Navigation Graph + Safe Args)
    - **Binding**: ViewBinding (NO findViewById)
    - **Chart**: MPAndroidChart
    - **Ekspor**: iText / PdfDocument API (PDF), OpenCSV (CSV)
    - **Foto**: Glide (loading & caching)
    - **OCR**: Google ML Kit Text Recognition
    - **Kamera**: CameraX
    - **Notifikasi**: WorkManager / AlarmManager
    - **Font**: Plus Jakarta Sans (via `res/font/`)

    ---

    ## Arsitektur

    Pendekatan **2-layer simpel**: data → UI.

    ```
    app/
    ├── data/
    │   ├── local/
    │   │   ├── entity/        # Room Entity (Vehicle, FuelLog, ServiceLog, VehicleDocument)
    │   │   ├── dao/            # Room DAO per tabel
    │   │   └── AppDatabase.kt # Room singleton
    │   ├── repository/         # query logic, return Result<T> / sealed Resource
    │   └── model/               # data class (UI model)
    ├── ui/
    │   ├── onboarding/    vehicle/    home/
    │   ├── fuel/          service/    docs/
    │   ├── scan/          notifications/  reports/
    │   ├── profile/
    │   └── common/
    │       ├── adapter/         # RecyclerView Adapter + DiffUtil
    │       └── viewmodel/        # ViewModel per fitur
    ├── util/             # Formatters, ImageHelper, ReceiptParser, ExportHelper
    └── service/          # NotificationScheduler (WorkManager/AlarmManager)
    ```

    **Konvensi**:
    - DI manual via constructor (NO Hilt/Koin).
    - MVVM: UI Event → ViewModel → StateFlow/LiveData → Fragment observe.
    - Repository return `Result<T>` / sealed class `Resource` — error & data terpisah jelas.
    - Foto disimpan di app-specific directory, di-compress 1280px / 80% sebelum simpan.
    - RecyclerView wajib pakai `DiffUtil` untuk update list.
    - ViewBinding di setiap Fragment/Activity.

    ---

    ## Database

    4 tabel utama + 1 tabel opsional:

    | Tabel | Isi |
    |---|---|
    | `vehicles` | Master kendaraan, multi-kendaraan per device |
    | `fuel_logs` | Catatan pengisian BBM, link `vehicle_id` |
    | `service_logs` | Catatan servis + `next_service_date` untuk reminder |
    | `vehicle_documents` | STNK/Pajak/dll + `expiry_date` untuk reminder |
    | `expense_categories` | Kategori pengeluaran (opsional) |

    DB dikelola via Room, singleton `AppDatabase`, disimpan di internal storage app.

    ---

    ## Logic Notifikasi Lokal

    ### Kapan dijadwalkan
    Tiga slot otomatis dijadwalkan setiap kali user **menyimpan / mengubah** record yang punya tanggal jatuh tempo:
    - **H-7** (7 hari sebelum)
    - **H-1** (sehari sebelum)
    - **H-0** (hari H, pagi)

    Semuanya fire **jam 09:00 waktu lokal device**. Slot yang sudah lewat saat scheduling **di-skip otomatis** (mis. user catat dokumen yang habis 3 hari lagi → cuma H-1 & H-0 yang masuk antrian).

    Triggernya hidup di ViewModel/Repository:
    - `ServiceViewModel.addOrUpdate()` → schedule pakai `next_service_date`
    - `ServiceViewModel.delete()` → cancel set notif untuk id itu
    - `DocumentViewModel.addOrUpdate()` → schedule pakai `expiry_date`
    - `DocumentViewModel.delete()` → cancel

    ### ID namespacing (anti-collision)
    Notification ID harus int32 dan stable agar bisa di-cancel/reschedule. Format:

    ```
    service log     : 10_000_000 + (serviceId * 10) + dayCode
    vehicle document: 20_000_000 + (documentId * 10) + dayCode
    ```

    `dayCode` ∈ `{0, 1, 7}`. Kapasitas ~1jt record per kategori, tidak akan collide dalam pemakaian normal.

    ### Scheduling mode
    Pakai `AlarmManager.setExactAndAllowWhileIdle` atau `WorkManager` (PeriodicWorkRequest) sesuai kebutuhan toleransi waktu. Toleransi beberapa menit sudah cukup memadai untuk reminder dokumen/servis.

    `BroadcastReceiver` boot (`RECEIVE_BOOT_COMPLETED`) didaftarkan di `AndroidManifest.xml` agar antrian notif survive restart device.

    ### In-app reminder list
    Halaman `NotificationsFragment` menggabungkan reminder yang sama:
    - Service log dengan `next_service_date` mendatang
    - Document dengan `expiry_date` dalam 30 hari ke depan

    Urut berdasarkan `daysUntil` ascending. Card paling urgent (≤7 hari) di-tint merah (`tintRose`), sisanya amber.

    ---

    ## Logic Backup & Restore

    | Aksi | Yang terjadi |
    |---|---|
    | **Backup** | Copy database Room → `getExternalFilesDir(null)/kendaraanku_backup_<timestamp>.db` |
    | **Restore** | Copy file backup terpilih → menimpa database aktif. Snackbar minta user restart app |
    | **Kirim** | `Intent.ACTION_SEND` dengan `FileProvider` → buka share sheet → pilih WhatsApp/Drive/Gmail/Telegram |
    | **Hapus** | `File.delete()` lalu refresh list |

    Catatan: backup hanya berisi file database mentah. Foto pendukung (struk, foto kendaraan) **belum** ter-include — file di app directory.

    ---

    ## Setup

    ```bash
    # Buka project di Android Studio
    # Sync Gradle
    ./gradlew build
    ```

    Jalankan aplikasi melalui Android Studio (Run) atau:

    ```bash
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
    | `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM` | Untuk reminder presisi via AlarmManager |

    Permission notifikasi direquest di `MainActivity.onCreate()` (sekali waktu pertama buka app).

    ---

    ## Navigation

    Pakai **Navigation Component**. Aturan penting:
    - `MainActivity` menggunakan `BottomNavigationView` + `NavHostFragment` dengan 4 tab utama (Home, Kendaraan, Laporan, Profil).
    - Tombol back ditangani via `OnBackPressedCallback`: dari tab non-Home → balik ke tab Home, dari Home → minimize app (`moveTaskToBack`).
    - Navigasi antar Fragment menggunakan `NavController` + Safe Args untuk passing data.
    - Shared ViewModel (scope ke `NavGraph` atau `Activity`) digunakan untuk data yang perlu diakses lintas Fragment.

    ---

    ## Convention

    - Bahasa UI: **Indonesia**.
    - Numeric: tabular figures (Plus Jakarta Sans).
    - Currency: `Formatters.currency(value)` → `Rp 1.250.000`.
    - Date: `Formatters.date(date)` → format `'dd MMMM yyyy'`, locale `id_ID`.

    ---

    ## Dokumentasi tambahan

    - **PRD**: `docs/prd/KendaraanKu_PRD_v1.1.txt`
    - **Design**: `docs/design/kendaraanku/`
    - **Rules & dev plan**: `.claude/rules.md`, `.claude/development-plan.md`
    - **Overview**: `docs/kendaraanku_overview.md` / `.html` / `.pdf`
