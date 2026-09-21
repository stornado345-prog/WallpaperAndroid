package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class WallpaperRepository(
    private val wallpaperDao: WallpaperDao,
    private val context: Context
) {
    val allWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getAllWallpapers()
    val favoriteWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getFavoriteWallpapers()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = wallpaperDao.getCount()
        if (count == 0) {
            val initialList = listOf(
                WallpaperEntity(
                    title = "Aventador SVJ • Liberty Widebody",
                    prompt = "Hyper-luxury widebody matte black Lamborghini Aventador with exposed forged carbon aerodynamic kit, vented hood, glowing amber LED daytime lights on wet asphalt reflecting neon lights in Tokyo at night",
                    category = "Hypercar",
                    modificationStyle = "Forged Carbon Aero & Liberty Widebody",
                    drawableResName = "img_wall_widebody_lambo",
                    isFavorite = true,
                    resolution = "1080x1920 (FHD+)",
                    aspectRatio = "9:16",
                    isAiGenerated = false,
                    createdAt = System.currentTimeMillis() - 10000
                ),
                WallpaperEntity(
                    title = "GT-R Nismo • Midnight Cyber Spec",
                    prompt = "Modified luxury Nissan GT-R Nismo with custom metallic midnight purple iridescent wrap, rose gold deep dish forged wheels, titanium quad exhaust tips, neon underglow in rain-slicked Shibuya crossing",
                    category = "Super GT",
                    modificationStyle = "Chameleon Wrap & Rose Gold Rims",
                    drawableResName = "img_wall_cyber_gtr",
                    isFavorite = true,
                    resolution = "1080x1920 (FHD+)",
                    aspectRatio = "9:16",
                    isAiGenerated = false,
                    createdAt = System.currentTimeMillis() - 20000
                ),
                WallpaperEntity(
                    title = "911 GT3 RS • Carbon Alpine Weapon",
                    prompt = "Custom modified Porsche 911 GT3 RS with swan neck carbon wing, racing yellow accents and roll cage, widebody aerodynamic fender vents on a scenic Alpine mountain pass at sunrise",
                    category = "Track Weapon",
                    modificationStyle = "Swan Neck Aero Wing & Carbon Vents",
                    drawableResName = "img_wall_gt3_track",
                    isFavorite = false,
                    resolution = "1080x1920 (FHD+)",
                    aspectRatio = "9:16",
                    isAiGenerated = false,
                    createdAt = System.currentTimeMillis() - 30000
                ),
                WallpaperEntity(
                    title = "Rolls-Royce Phantom • VIP Bespoke Stance",
                    prompt = "Bespoke luxury VIP stanced Rolls-Royce Phantom with custom two-tone brushed rose gold and black diamond metallic finish, monoblock forged aero disc wheels, parked outside an architectural luxury villa at twilight",
                    category = "VIP Luxury",
                    modificationStyle = "Bespoke Two-Tone & Forged Aero Discs",
                    drawableResName = "img_wall_rolls_vip",
                    isFavorite = false,
                    resolution = "1080x1920 (FHD+)",
                    aspectRatio = "9:16",
                    isAiGenerated = false,
                    createdAt = System.currentTimeMillis() - 40000
                )
            )
            wallpaperDao.insertAll(initialList)
        }
    }

    suspend fun insertWallpaper(wallpaper: WallpaperEntity): Long = withContext(Dispatchers.IO) {
        wallpaperDao.insert(wallpaper)
    }

    suspend fun updateWallpaper(wallpaper: WallpaperEntity) = withContext(Dispatchers.IO) {
        wallpaperDao.update(wallpaper)
    }

    suspend fun toggleFavorite(wallpaper: WallpaperEntity) = withContext(Dispatchers.IO) {
        wallpaperDao.updateFavorite(wallpaper.id, !wallpaper.isFavorite)
    }

    suspend fun deleteWallpaper(wallpaper: WallpaperEntity) = withContext(Dispatchers.IO) {
        // Delete local file if present
        wallpaper.filePath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) { }
        }
        wallpaperDao.delete(wallpaper)
    }

    suspend fun getWallpaperById(id: Long): WallpaperEntity? = withContext(Dispatchers.IO) {
        wallpaperDao.getWallpaperById(id)
    }
}
