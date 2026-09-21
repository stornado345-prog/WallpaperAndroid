package com.example.util

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

enum class WallpaperTarget {
    HOME_SCREEN,
    LOCK_SCREEN,
    BOTH
}

object WallpaperHelper {

    suspend fun applyWallpaper(
        context: Context,
        bitmap: Bitmap,
        target: WallpaperTarget
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val whichFlag = when (target) {
                    WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setBitmap(bitmap, null, true, whichFlag)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBitmapToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val directory = File(context.filesDir, "wallpapers")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "$fileName.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportToGallery(
        context: Context,
        bitmap: Bitmap
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val filename = "ApexCraft_${System.currentTimeMillis()}.png"
            val outputStream: OutputStream?
            val imageUri: Uri?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + "ApexCraft"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("Failed to create MediaStore entry")
                outputStream = resolver.openOutputStream(imageUri)
                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "ApexCraft")
                if (!appDir.exists()) appDir.mkdirs()
                val imageFile = File(appDir, filename)
                outputStream = FileOutputStream(imageFile)
                outputStream.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                imageUri = Uri.fromFile(imageFile)
            }
            Result.success(imageUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadBitmap(context: Context, filePath: String?, drawableResName: String?): Bitmap? {
        return try {
            if (!filePath.isNullOrBlank()) {
                val file = File(filePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else null
            } else if (!drawableResName.isNullOrBlank()) {
                val resId = context.resources.getIdentifier(
                    drawableResName,
                    "drawable",
                    context.packageName
                )
                if (resId != 0) {
                    BitmapFactory.decodeResource(context.resources, resId)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun shareWallpaper(context: Context, bitmap: Bitmap, title: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_wallpaper.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri: Uri = try {
                FileProvider.getUriForFile(context, authority, file)
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "ApexCraft Luxury Car Wallpaper - $title")
                putExtra(Intent.EXTRA_TEXT, "Check out this custom luxury modified car wallpaper: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            // Log or fallback
        }
    }
}
