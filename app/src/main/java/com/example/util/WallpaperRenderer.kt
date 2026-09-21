package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import com.carwalpaperandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CustomModSpec(
    val carModel: String,
    val category: String,
    val modStyle: String,
    val paintColor: String,
    val wheelType: String,
    val environment: String,
    val customPrompt: String
)

object WallpaperRenderer {

    suspend fun renderCustomModifiedWallpaper(
        context: Context,
        spec: CustomModSpec
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Select best base drawable depending on car category or model
        val baseDrawableId = when {
            spec.category.contains("Hypercar", ignoreCase = true) || spec.carModel.contains("Lambo", ignoreCase = true) ->
                R.drawable.img_wall_widebody_lambo
            spec.category.contains("Super GT", ignoreCase = true) || spec.carModel.contains("GT-R", ignoreCase = true) ->
                R.drawable.img_wall_cyber_gtr
            spec.category.contains("Track", ignoreCase = true) || spec.carModel.contains("Porsche", ignoreCase = true) ->
                R.drawable.img_wall_gt3_track
            spec.category.contains("VIP", ignoreCase = true) || spec.carModel.contains("Rolls", ignoreCase = true) ->
                R.drawable.img_wall_rolls_vip
            else -> R.drawable.img_wall_widebody_lambo
        }

        val baseBitmap = WallpaperHelper.loadBitmap(context, null, context.resources.getResourceEntryName(baseDrawableId))

        if (baseBitmap != null) {
            // Draw scaled to fill aspect ratio
            val srcRect = Rect(0, 0, baseBitmap.width, baseBitmap.height)
            val dstRect = Rect(0, 0, width, height)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(baseBitmap, srcRect, dstRect, paint)
        } else {
            // Fallback dark gradient canvas
            val darkPaint = Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    Color.parseColor("#12151D"), Color.parseColor("#0A0C10"),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), darkPaint)
        }

        // Apply customized atmospheric tint and glow based on paint / mod style
        val tintPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        }

        when {
            spec.paintColor.contains("Gold", ignoreCase = true) || spec.modStyle.contains("Gold", ignoreCase = true) -> {
                tintPaint.shader = LinearGradient(
                    0f, height * 0.4f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#00000000"), Color.parseColor("#44FFB800")),
                    null, Shader.TileMode.CLAMP
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), tintPaint)
            }
            spec.paintColor.contains("Purple", ignoreCase = true) || spec.modStyle.contains("Cyber", ignoreCase = true) -> {
                tintPaint.shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#228A2BE2"), Color.parseColor("#4400E5FF")),
                    null, Shader.TileMode.CLAMP
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), tintPaint)
            }
            spec.paintColor.contains("Red", ignoreCase = true) || spec.modStyle.contains("Track", ignoreCase = true) -> {
                tintPaint.shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#00000000"), Color.parseColor("#44FF2244")),
                    null, Shader.TileMode.CLAMP
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), tintPaint)
            }
        }

        // Apply dark vignette on edges for lock screen readability
        val vignettePaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(
                    Color.parseColor("#88000000"),
                    Color.parseColor("#00000000"),
                    Color.parseColor("#00000000"),
                    Color.parseColor("#AA000000")
                ),
                floatArrayOf(0f, 0.25f, 0.75f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)

        // Lower subtle carbon spec watermark watermark
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CCFFFFFF")
            textSize = 28f
            letterSpacing = 0.25f
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#AAFFB800")
            textSize = 20f
            letterSpacing = 0.35f
        }

        val badgeText = "${spec.carModel.uppercase()} • ${spec.modStyle.uppercase()}"
        val subText = "APEXCRAFT • ${spec.wheelType.uppercase()} • ULTRA HD"
        
        canvas.drawText(badgeText, 54f, height - 90f, badgePaint)
        canvas.drawText(subText, 54f, height - 60f, subPaint)

        bitmap
    }
}
