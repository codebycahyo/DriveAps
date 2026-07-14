package com.example.kendaraanbp1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.kendaraanbp1.databinding.ActivityMainBinding
import com.example.kendaraanbp1.util.PermissionHelper
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.kendaraanbp1.util.SettingsManager.applySettings(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
        
        // Request Notifications Permission (Android 13+)
        PermissionHelper.checkAndRequestNotificationPermission(this, requestNotificationPermissionLauncher)
        
        // Check Exact Alarm Permission (Android 12+)
        PermissionHelper.checkAndRequestExactAlarmPermission(this) { intent ->
            startActivity(intent)
        }
    }
}
