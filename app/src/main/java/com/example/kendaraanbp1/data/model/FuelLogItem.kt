package com.example.kendaraanbp1.data.model

data class FuelLogItem(
    val id: Long,
    val stationName: String,
    val date: String,
    val amountLabel: String,
    val litersLabel: String,
    val odometerLabel: String,
)
