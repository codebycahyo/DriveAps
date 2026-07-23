package com.example.kendaraanbp1.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A locally-stored user account. The app is 100% offline, so accounts live only
 * in this device's Room database. Email is unique (one account per address) and
 * [passwordHash] stores a salted, iterated hash — never the raw password.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)
