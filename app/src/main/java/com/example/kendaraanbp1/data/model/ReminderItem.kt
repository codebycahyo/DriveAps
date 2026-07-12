package com.example.kendaraanbp1.data.model

import androidx.annotation.DrawableRes

enum class ReminderSeverity {
    URGENT,
    STANDARD,
}

data class ReminderItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    @DrawableRes val iconRes: Int,
    val severity: ReminderSeverity,
)
