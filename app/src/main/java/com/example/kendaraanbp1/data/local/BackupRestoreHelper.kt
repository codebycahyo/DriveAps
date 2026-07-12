package com.example.kendaraanbp1.data.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// SQLite file magic header: "SQLite format 3\000" (first 16 bytes)
private val SQLITE_HEADER = byteArrayOf(
    0x53, 0x51, 0x4C, 0x69, 0x74, 0x65, 0x20, 0x66,
    0x6F, 0x72, 0x6D, 0x61, 0x74, 0x20, 0x33, 0x00
)

object BackupRestoreHelper {

    private const val DB_NAME = "kendaraanku_database"
    private const val BACKUP_PREFIX = "kendaraanku_backup_"
    private const val BACKUP_SUFFIX = ".db"

    fun getBackupDirectory(context: Context): File? {
        return context.getExternalFilesDir(null)
    }

    fun getBackupFiles(context: Context): List<File> {
        val dir = getBackupDirectory(context) ?: return emptyList()
        return dir.listFiles { _, name ->
            name.startsWith(BACKUP_PREFIX) && name.endsWith(BACKUP_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    fun createBackup(context: Context): Result<File> {
        try {
            val dbFile = context.getDatabasePath(DB_NAME)
            if (!dbFile.exists()) {
                return Result.failure(Exception("Database file does not exist."))
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "$BACKUP_PREFIX$timestamp$BACKUP_SUFFIX"
            val backupDir = getBackupDirectory(context) ?: return Result.failure(Exception("Cannot access external files dir"))
            val backupFile = File(backupDir, backupFileName)

            copyFile(dbFile, backupFile)
            
            val walFile = File(dbFile.path + "-wal")
            if (walFile.exists()) {
                copyFile(walFile, File(backupDir, "$BACKUP_PREFIX$timestamp-wal"))
            }
            
            val shmFile = File(dbFile.path + "-shm")
            if (shmFile.exists()) {
                copyFile(shmFile, File(backupDir, "$BACKUP_PREFIX$timestamp-shm"))
            }

            return Result.success(backupFile)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    fun restoreBackup(context: Context, backupFile: File): Result<Unit> {
        try {
            // STEP 1: Validate that the file is a real SQLite database, not a random/corrupt file
            if (!isValidSqliteFile(backupFile)) {
                return Result.failure(Exception("File bukan database yang valid. Restore dibatalkan untuk melindungi data Anda."))
            }

            val dbFile = context.getDatabasePath(DB_NAME)

            // STEP 2: Close the active database connection before overwriting the file.
            // This prevents WAL-mode corruption from open file handles.
            AppDatabase.closeDatabase()

            // STEP 3: Overwrite db
            copyFile(backupFile, dbFile)
            
            // Overwrite or delete WAL and SHM to prevent corruption
            val timestamp = backupFile.name.substringAfter(BACKUP_PREFIX).substringBefore(BACKUP_SUFFIX)
            
            val backupWal = File(backupFile.parentFile, "$BACKUP_PREFIX$timestamp-wal")
            val dbWal = File(dbFile.path + "-wal")
            if (backupWal.exists()) {
                copyFile(backupWal, dbWal)
            } else if (dbWal.exists()) {
                dbWal.delete()
            }

            val backupShm = File(backupFile.parentFile, "$BACKUP_PREFIX$timestamp-shm")
            val dbShm = File(dbFile.path + "-shm")
            if (backupShm.exists()) {
                copyFile(backupShm, dbShm)
            } else if (dbShm.exists()) {
                dbShm.delete()
            }

            return Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    /**
     * Validates that a file is a genuine SQLite database by checking the first 16 bytes
     * against the known SQLite magic header string.
     */
    fun isValidSqliteFile(file: File): Boolean {
        if (!file.exists() || file.length() < 16) return false
        return try {
            FileInputStream(file).use { stream ->
                val header = ByteArray(16)
                val bytesRead = stream.read(header)
                bytesRead == 16 && header.contentEquals(SQLITE_HEADER)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun deleteBackup(backupFile: File): Boolean {
        var deleted = false
        if (backupFile.exists()) {
            deleted = backupFile.delete()
        }
        val timestamp = backupFile.name.substringAfter(BACKUP_PREFIX).substringBefore(BACKUP_SUFFIX)
        val backupWal = File(backupFile.parentFile, "$BACKUP_PREFIX$timestamp-wal")
        if (backupWal.exists()) backupWal.delete()
        
        val backupShm = File(backupFile.parentFile, "$BACKUP_PREFIX$timestamp-shm")
        if (backupShm.exists()) backupShm.delete()
        
        return deleted
    }

    fun getBackupUriForShare(context: Context, backupFile: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun copyFile(src: File, dst: File) {
        FileInputStream(src).use { inStream ->
            FileOutputStream(dst).use { outStream ->
                inStream.copyTo(outStream)
            }
        }
    }
}
