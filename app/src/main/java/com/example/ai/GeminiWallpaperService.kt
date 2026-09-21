package com.example.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.carwalpaperandroid.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiWallpaperService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun hasApiKey(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (_: Exception) {
            false
        }
    }

    suspend fun enhancePrompt(
        userIdea: String,
        category: String,
        modStyle: String,
        environment: String,
        wheels: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // High-end offline prompt synthesis
            return@withContext "High-end modified $userIdea ($category), featuring bespoke $modStyle aerodynamic kit, $wheels forged monoblock deep dish rims, aggressive stanced fitment, quad titanium exhaust tips, illuminated LED signature, parked in $environment, rain slicked pavement with neon raytraced reflections, 8k cinematic vertical wallpaper."
        }

        try {
            val endpoint = "$BASE_URL/gemini-1.5-flash:generateContent?key=$apiKey"
            val promptRequest = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", "You are an expert luxury automotive designer and photographer. Take this car modification description and turn it into a single, cohesive, vivid vertical phone wallpaper prompt (maximum 60 words). Specify widebody/aero, custom paint/finish, forged wheels, lighting and background atmosphere. Base info: Car: '$userIdea', Category: '$category', Mod style: '$modStyle', Wheels: '$wheels', Environment: '$environment'. Respond with only the final prompt.")
                    }))
                }))
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(promptRequest.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val candidates = json.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")
                    ?.trim()
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
        } catch (_: Exception) {
            // Fallback to local synthesizer
        }

        "Ultra high-definition modified $userIdea, featuring bespoke $modStyle, $wheels forged rims with stanced camber, aerodynamic forged carbon diffusers, parked in $environment at night with realistic reflections, 8k luxury wallpaper."
    }

    suspend fun generateWallpaper(
        finalPrompt: String,
        aspectRatio: String = "9:16"
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your GEMINI_API_KEY in the Secrets panel.")
            )
        }

        try {
            // Using gemini-2.0-flash-exp for image generation
            val endpoint = "$BASE_URL/gemini-2.0-flash-exp:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", finalPrompt)
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", aspectRatio)
                        put("imageSize", "1K")
                    })
                    put("responseModalities", JSONArray().put("IMAGE").put("TEXT"))
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("API Error (${response.code}): $responseBody")
                )
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No candidates returned by model"))
            }

            val parts = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")

            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val inlineData = part.optJSONObject("inlineData")
                if (inlineData != null) {
                    val base64Data = inlineData.optString("data")
                    if (base64Data.isNotBlank()) {
                        val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        if (bitmap != null) {
                            return@withContext Result.success(bitmap)
                        }
                    }
                }
            }

            Result.failure(Exception("No image part received in model response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
