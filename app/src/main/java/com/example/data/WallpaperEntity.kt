package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val category: String, // "Hypercar", "Track Spec", "VIP Stance", "Cyber Restomod", "Super GT"
    val modificationStyle: String, // e.g. "Forged Carbon Aero", "Widebody SVJ", "VIP Stance", "Cyber Midnight"
    val filePath: String? = null, // Local file path on disk
    val drawableResName: String? = null, // Drawable name for bundled showcase items
    val isFavorite: Boolean = false,
    val resolution: String = "1080x1920 (FHD+)",
    val aspectRatio: String = "9:16",
    val isAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
