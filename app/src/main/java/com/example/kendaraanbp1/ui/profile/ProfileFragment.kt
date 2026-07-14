package com.example.kendaraanbp1.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.FragmentProfileBinding
import com.example.kendaraanbp1.databinding.ItemSettingsRowBinding
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.applyTopSystemBarPadding
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setupNavigation
import com.example.kendaraanbp1.data.local.BackupRestoreHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import androidx.fragment.app.activityViewModels
import com.example.kendaraanbp1.util.ExportHelper
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.applyTopSystemBarPadding()
        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.PROFILE)
        binding.bottomNavInclude.setupNavigation(findNavController())

        setupRows()
        
        binding.rowBackupRestore.root.setOnClickListener {
            showBackupRestoreDialog()
        }

        binding.rowExportReport.root.setOnClickListener {
            showExportVehicleSelectionDialog()
        }

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build())
        }

        observeVehicleCount()
    }

    private fun observeVehicleCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.allVehicles.collect { resource ->
                if (resource is com.example.kendaraanbp1.data.repository.Resource.Success) {
                    val count = resource.data?.size ?: 0
                    binding.profileVehicleCount.text = getString(R.string.profile_vehicle_count_fmt, count)
                }
            }
        }
    }

    private fun setupRows() {
        bindRow(binding.rowEditProfile, R.drawable.ic_user, getString(R.string.row_edit_profile))
        bindRow(binding.rowSecurity, R.drawable.ic_shield, getString(R.string.row_security))
        
        // Theme
        val currentThemeMode = com.example.kendaraanbp1.util.SettingsManager.getThemeMode(requireContext())
        val themeValue = when (currentThemeMode) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO -> "Terang"
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES -> "Gelap"
            else -> "Sistem"
        }
        bindRow(binding.rowTheme, R.drawable.ic_palette, getString(R.string.row_theme), themeValue)
        binding.rowTheme.root.setOnClickListener { showThemeDialog() }
        
        // Language
        val currentLang = com.example.kendaraanbp1.util.SettingsManager.getLanguage(requireContext())
        val langValue = if (currentLang == "id") "Bahasa Indonesia" else "English"
        bindRow(binding.rowLanguage, R.drawable.ic_globe, getString(R.string.row_language), langValue)
        binding.rowLanguage.root.setOnClickListener { showLanguageDialog() }
        
        bindRow(binding.rowNotifications, R.drawable.ic_bell, getString(R.string.row_notifications))
        bindRow(binding.rowBackupRestore, R.drawable.ic_cloud_upload, getString(R.string.row_backup_restore))
        bindRow(binding.rowExportReport, R.drawable.ic_file_down, getString(R.string.row_export_report), getString(R.string.row_export_report_value))
        bindRow(binding.rowHelp, R.drawable.ic_help_circle, getString(R.string.row_help))
        bindRow(binding.rowAbout, R.drawable.ic_info, getString(R.string.row_about))
        bindRow(binding.rowPrivacyPolicy, R.drawable.ic_lock, getString(R.string.row_privacy_policy))
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Sistem Default", "Terang", "Gelap")
        val currentThemeMode = com.example.kendaraanbp1.util.SettingsManager.getThemeMode(requireContext())
        val checkedItem = when (currentThemeMode) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO -> 1
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES -> 2
            else -> 0
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Tema")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                val newMode = when (which) {
                    1 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                    2 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                    else -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                com.example.kendaraanbp1.util.SettingsManager.setThemeMode(requireContext(), newMode)
                setupRows() // Update the row text
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Bahasa Indonesia", "English")
        val currentLang = com.example.kendaraanbp1.util.SettingsManager.getLanguage(requireContext())
        val checkedItem = if (currentLang == "id") 0 else 1

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Bahasa")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val newLang = if (which == 0) "id" else "en"
                com.example.kendaraanbp1.util.SettingsManager.setLanguage(requireContext(), newLang)
                setupRows() // Update the row text
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showBackupRestoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.row_backup_restore))
            .setMessage("Catatan eksplisit: Backup saat ini HANYA mencakup data teks (database). Foto struk dan kendaraan belum termasuk.")
            .setPositiveButton("Buat Backup Baru") { _, _ ->
                createBackup()
            }
            .setNeutralButton("Kelola File Backup") { _, _ ->
                showBackupListDialog()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createBackup() {
        val result = BackupRestoreHelper.createBackup(requireContext())
        if (result.isSuccess) {
            Toast.makeText(requireContext(), "Backup berhasil disimpan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Gagal membuat backup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBackupListDialog() {
        val files = BackupRestoreHelper.getBackupFiles(requireContext())
        if (files.isEmpty()) {
            Toast.makeText(requireContext(), "Belum ada file backup", Toast.LENGTH_SHORT).show()
            return
        }

        val fileNames = files.map { file ->
            try {
                // Parse "drivetrack_backup_yyyyMMdd_HHmmss.db" or old prefix
                val prefix = if (file.name.startsWith("drivetrack_backup_")) "drivetrack_backup_" else "kendaraanku_backup_"
                val datePart = file.name.removePrefix(prefix).removeSuffix(".db")
                val inFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val outFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
                val date = inFormat.parse(datePart)
                if (date != null) "Backup: ${outFormat.format(date)}" else file.name
            } catch (e: Exception) {
                file.name
            }
        }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih File Backup")
            .setItems(fileNames) { _, which ->
                val selectedFile = files[which]
                showActionDialogForFile(selectedFile)
            }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun showActionDialogForFile(file: File) {
        val actions = arrayOf("Restore", "Bagikan", "Hapus")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aksi untuk ${file.name}")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> restoreBackup(file)
                    1 -> shareBackup(file)
                    2 -> deleteBackup(file)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun restoreBackup(file: File) {
        val result = BackupRestoreHelper.restoreBackup(requireContext(), file)
        if (result.isSuccess) {
            Snackbar.make(binding.root, "Restore berhasil. Silakan restart aplikasi.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Tutup") { }
                .show()
        } else {
            Toast.makeText(requireContext(), "Gagal restore backup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareBackup(file: File) {
        val uri = BackupRestoreHelper.getBackupUriForShare(requireContext(), file)
        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Bagikan File Backup"))
        } else {
            Toast.makeText(requireContext(), "Gagal menyiapkan file untuk dibagikan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBackup(file: File) {
        if (BackupRestoreHelper.deleteBackup(file)) {
            Toast.makeText(requireContext(), "Backup dihapus", Toast.LENGTH_SHORT).show()
            // Refresh the list after deleting
            showBackupListDialog()
        } else {
            Toast.makeText(requireContext(), "Gagal menghapus backup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showExportVehicleSelectionDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val resource = sharedViewModel.allVehicles.value
            if (resource is com.example.kendaraanbp1.data.repository.Resource.Success) {
                val vehicles = resource.data ?: emptyList()
                if (vehicles.isEmpty()) {
                    Toast.makeText(requireContext(), "Belum ada kendaraan", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val vehicleNames: Array<CharSequence> = vehicles.map { "${it.brand} ${it.model}" }.toTypedArray()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Pilih Kendaraan untuk Diekspor")
                    .setItems(vehicleNames) { _, which ->
                        showExportFormatDialog(vehicles[which])
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Gagal memuat daftar kendaraan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showExportFormatDialog(vehicle: VehicleEntity) {
        val formats = arrayOf("Export ke PDF", "Export ke CSV")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Format")
            .setItems(formats) { _, which ->
                when (which) {
                    0 -> exportData(vehicle, true)
                    1 -> exportData(vehicle, false)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun exportData(vehicle: VehicleEntity, isPdf: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val fuelResource = profileViewModel.getFuelHistory(vehicle.id)
            val serviceResource = profileViewModel.getServiceHistory(vehicle.id)
            
            val fuelList = fuelResource.data ?: emptyList()
            val serviceList = serviceResource.data ?: emptyList()
            
            val uri = if (isPdf) {
                ExportHelper.exportToPdf(requireContext(), vehicle, fuelList, serviceList)
            } else {
                ExportHelper.exportToCsv(requireContext(), vehicle, fuelList, serviceList)
            }
            
            if (uri != null) {
                shareExportedFile(uri, if (isPdf) "application/pdf" else "text/csv")
            } else {
                Toast.makeText(requireContext(), "Gagal membuat laporan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareExportedFile(uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Bagikan Laporan"))
    }

    private fun bindRow(
        row: ItemSettingsRowBinding,
        iconRes: Int,
        label: String,
        value: String? = null,
    ) {
        row.rowIcon.setImageResource(iconRes)
        row.rowLabel.text = label
        row.rowValue.text = value.orEmpty()
        row.rowValue.visibility = if (value == null) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
