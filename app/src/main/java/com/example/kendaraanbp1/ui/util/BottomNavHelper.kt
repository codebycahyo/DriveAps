package com.example.kendaraanbp1.ui.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.LayoutBottomNavBinding
import androidx.navigation.NavController
import androidx.navigation.NavOptions

enum class BottomNavTab { HOME, VEHICLES, REPORTS, PROFILE }

private class NavItemViews(
    val indicator: android.view.View,
    val icon: ImageView,
    val label: TextView,
)

fun LayoutBottomNavBinding.setActiveTab(tab: BottomNavTab) {
    val items = mapOf(
        BottomNavTab.HOME to NavItemViews(navHomeIndicator, navHomeIcon, navHomeLabel),
        BottomNavTab.VEHICLES to NavItemViews(navVehiclesIndicator, navVehiclesIcon, navVehiclesLabel),
        BottomNavTab.REPORTS to NavItemViews(navReportsIndicator, navReportsIcon, navReportsLabel),
        BottomNavTab.PROFILE to NavItemViews(navProfileIndicator, navProfileIcon, navProfileLabel),
    )
    items.forEach { (itemTab, views) ->
        val active = itemTab == tab
        val context = views.icon.context
        if (active) {
            views.indicator.setBackgroundResource(R.drawable.bg_nav_indicator)
        } else {
            views.indicator.background = null
        }
        val contentColor = if (active) R.color.brand_ink else R.color.text_placeholder
        views.icon.imageTintList = ContextCompat.getColorStateList(context, contentColor)
        views.label.setTextColor(ContextCompat.getColor(context, contentColor))
        
        views.indicator.isSelected = active
        views.icon.isSelected = active
        views.label.isSelected = active
    }
}

fun LayoutBottomNavBinding.setupNavigation(navController: NavController) {
    val navOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(R.id.homeDashboardFragment, inclusive = false, saveState = true)
        .build()

    val homeNavOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(R.id.homeDashboardFragment, inclusive = false, saveState = true)
        .build()

    root.findViewById<android.view.View>(R.id.navHome).setOnClickListener {
        navController.navigate(R.id.homeDashboardFragment, null, homeNavOptions)
    }

    root.findViewById<android.view.View>(R.id.navVehicles).setOnClickListener {
        navController.navigate(R.id.vehicleDetailFragment, null, navOptions)
    }

    root.findViewById<android.view.View>(R.id.navReports).setOnClickListener {
        android.widget.Toast.makeText(
            it.context,
            R.string.reports_hint,
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    root.findViewById<android.view.View>(R.id.navProfile).setOnClickListener {
        navController.navigate(R.id.profileFragment, null, navOptions)
    }
}
