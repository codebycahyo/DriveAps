package com.example.kendaraanbp1.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class VehicleActivityItem(
    val id: Long,
    val title: String,
    val date: String,
    val subtitle: String,
    val amountLabel: String,
    @DrawableRes val iconRes: Int,
    @ColorRes val iconTintRes: Int,
    @ColorRes val iconBgRes: Int,
)
