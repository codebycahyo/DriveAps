 # Kendaraanku - Aplikasi Pencatatan Kendaraan Pribadi (Full Offline)

## Project Overview
Aplikasi Android native untuk mencatat seluruh aktivitas dan pengeluaran kendaraan pribadi secara offline.
Tugas final projek — dibangun dengan **Kotlin** dan **XML layout** (Android Views, bukan Jetpack Compose).

**PRD**: `docs/prd/KendaraanKu_PRD_v1.1.txt`
**Design**: `docs/design/kendaraanku/` (referensi visual & screenshot)
**Dev Plan**: `.claude/development-plan.md` (fase & checklist lengkap)
**Rules**: `.claude/rules.md` (architecture & coding conventions)

## Current Status
- UI Prototype: **COMPLETE** (12 screen layout XML hasil slicing design)
- Data Layer: **NOT STARTED** (semua data masih hardcoded / dummy)
- ViewModel Integration: **NOT STARTED**
- Database (Room): **NOT STARTED**

## Tech Stack (Android Native - Kotlin & XML)
- Kotlin + Android Views (XML layout, ConstraintLayout sebagai layout utama)
- Architecture: MVVM (ViewModel + LiveData/StateFlow)
- Room Database (SQLite) + SharedPreferences/DataStore
- Navigation Component (Navigation Graph + Fragment + Safe Args)
- ViewBinding (NO findViewById)
- MPAndroidChart untuk grafik
- ML Kit Text Recognition untuk OCR struk
- CameraX untuk scan nota
- Glide untuk image loading & caching
- iText / PdfDocument API (export PDF), OpenCSV (export CSV)
- WorkManager / AlarmManager untuk reminder notifikasi lokal
- Material Components for Android (Material 3)
- Plus Jakarta Sans (via `res/font/`)

## Architecture: 2-Layer Simpel (Data → UI)
```
app/
├── data/
│   ├── local/
│   │   ├── entity/        → Room Entity (Vehicle, FuelLog, ServiceLog, VehicleDocument)
│   │   ├── dao/            → Room DAO per tabel
│   │   └── AppDatabase.kt  → Room singleton
│   ├── repository/         → query logic, return Result<T> / sealed Resource
│   └── model/               → data class (UI model, mapping dari entity)
├── ui/
│   ├── onboarding/          → Fragment + layout XML
│   ├── vehicle/
│   ├── home/
│   ├── fuel/
│   ├── service/
│   ├── docs/
│   ├── scan/
│   ├── notifications/
│   ├── reports/
│   ├── profile/
│   └── common/
│       ├── adapter/         → RecyclerView Adapter + DiffUtil
│       └── viewmodel/        → ViewModel per fitur
├── util/
│   ├── Formatters.kt         → currency, date (tabular figures)
│   ├── ImageHelper.kt        → compress & save foto
│   ├── ReceiptParser.kt       → parsing hasil OCR
│   └── ExportHelper.kt        → export PDF/CSV
└── service/
    └── NotificationScheduler.kt → scheduler reminder (WorkManager/AlarmManager)
```

## Key Conventions
- NO auth/login (100% offline app)
- Onboarding → AddVehicle (first time) → Home
- MVVM: UI Event → ViewModel → StateFlow/LiveData → Fragment observe
- DI manual via constructor (NO Hilt/Koin/get_it)
- Navigation: Navigation Component (Navigation Graph + Safe Args), bukan manual Intent/FragmentTransaction
- Foto: compress 1280px/80%, simpan di app-specific directory
- Database: Room singleton, 4 tabel utama (vehicles, fuel_logs, service_logs, vehicle_documents) + expense_categories (opsional)
- Layout XML: gunakan ConstraintLayout sebagai root, hindari nested layout berlebih
- RecyclerView wajib pakai DiffUtil untuk update list
- ViewBinding di setiap Fragment/Activity, hindari findViewById
