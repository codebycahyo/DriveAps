# ProGuard/R8 rules untuk release build (minifyEnabled + shrinkResources).

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# --- Room ---
# Entity & data class yang di-serialize/di-refleksi harus di-keep.
-keep class com.example.kendaraanbp1.data.local.entity.** { *; }
-keep class com.example.kendaraanbp1.data.model.** { *; }
# Room menghasilkan kelas *_Impl; jangan diobfuscate.
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# --- ML Kit Text Recognition ---
# Model & internal ML Kit diakses via refleksi/JNI.
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }
-dontwarn com.google.mlkit.**

# --- CameraX ---
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# --- Kotlin coroutines ---
-dontwarn kotlinx.coroutines.**

# Baris debug info supaya stack trace crash tetap terbaca di Play Console.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
