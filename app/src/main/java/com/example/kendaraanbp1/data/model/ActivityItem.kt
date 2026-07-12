package com.example.kendaraanbp1.data.model

import androidx.annotation.DrawableRes

data class ActivityItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val amountLabel: String,
    @DrawableRes val iconRes: Int,
)
