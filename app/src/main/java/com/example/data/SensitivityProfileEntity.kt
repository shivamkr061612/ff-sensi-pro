package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensitivity_profiles")
data class SensitivityProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val game: String, // "Free Fire MAX" or "Free Fire India"
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int,
    val playStyle: String,
    val fingerCount: String,
    val deviceCategory: String,
    val dpiGuidance: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
