package com.example.kendaraanbp1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kendaraanbp1.data.local.dao.DocumentDao
import com.example.kendaraanbp1.data.local.dao.FuelLogDao
import com.example.kendaraanbp1.data.local.dao.ServiceLogDao
import com.example.kendaraanbp1.data.local.dao.UserDao
import com.example.kendaraanbp1.data.local.dao.VehicleDao
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import com.example.kendaraanbp1.data.local.entity.UserEntity
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity
import com.example.kendaraanbp1.data.local.entity.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        FuelLogEntity::class,
        ServiceLogEntity::class,
        VehicleDocumentEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelLogDao(): FuelLogDao
    abstract fun serviceLogDao(): ServiceLogDao
    abstract fun documentDao(): DocumentDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kendaraanku_database"
                )
                // v1 -> v2 adds the `users` table. The app is not yet published
                // (versionCode 1), so a destructive fallback is safe and avoids
                // hand-written migration SQL. Add a real Migration before release
                // if a published build ever needs to preserve data across schema changes.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Closes the active database connection and clears the singleton instance.
         * MUST be called before overwriting the database file (e.g., during restore),
         * to prevent WAL-mode corruption from open file handles.
         * After calling this, the next call to getDatabase() will re-open from the
         * (newly restored) file on disk.
         */
        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
