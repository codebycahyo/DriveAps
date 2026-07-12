package com.example.kendaraanbp1.data.model

import androidx.annotation.DrawableRes

enum class DocumentStatus { EXPIRING, VERIFIED, ACTIVE, URGENT }

data class DocumentItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val statusLabel: String,
    val status: DocumentStatus,
    @DrawableRes val iconRes: Int,
)
