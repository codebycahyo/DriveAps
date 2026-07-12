package com.example.kendaraanbp1.data.model

import androidx.annotation.DrawableRes

data class ServiceTimelineItem(
    val id: Long,
    val title: String,
    val workshopName: String,
    val statusLabel: String,
    val dateDistanceLabel: String,
    val totalCostLabel: String,
    @DrawableRes val iconRes: Int,
)
