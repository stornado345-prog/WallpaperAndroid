package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet

import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.carwalpaperandroid.R
import com.example.data.WallpaperEntity
import com.example.ui.PreviewOverlayMode
import com.example.ui.WallpaperViewModel
import com.example.ui.components.PhoneMockupOverlay
import com.example.util.WallpaperTarget
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperDetailScreen(
    wallpaper: WallpaperEntity,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewMode by viewModel.previewOverlay.collectAsStateWithLifecycle()
    val isApplying by viewModel.isApplying.collectAsStateWithLifecycle()

    var showApplySheet by remember { mutableStateOf(value = false) }
    var showSpecsSheet by remember { mutableStateOf(value = false) }

    val imageModel: Any = when {
        !wallpaper.filePath.isNullOrBlank() -> File(wallpaper.filePath)
        !wallpaper.drawableResName.isNullOrBlank() -> {
            val resId = context.resources.getIdentifier(
                wallpaper.drawableResName,
                "drawable",
                context.resources.getResourcePackageName(R.drawable.img_wall_widebody_lambo)
            )
            if (resId != 0) resId else R.drawable.img_wall_widebody_lambo
        }
        else -> R.drawable.img_wall_widebody_lambo
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
    ) {
        // Full Wallpaper Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageModel)
                .crossfade(true)
                .build(),
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Mockup Simulator Overlays (Lock Screen / Home Screen)
        PhoneMockupOverlay(
            mode = previewMode,
            modifier = Modifier.fillMaxSize()
        )

        // Top Controls Gradient & Bar
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Title info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = wallpaper.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "${wallpaper.category} • ${wallpaper.resolution}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB800)
                    )
                }

                // Actions: Specs, Share, Favorite
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showSpecsSheet = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("specs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Specs",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.shareWallpaper(wallpaper) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.toggleFavorite(wallpaper) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (wallpaper.isFavorite) Color(0xFFFF3366) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Bottom Controls Gradient & Action Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Mockup Simulator Mode Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val modes = listOf(
                        Triple(PreviewOverlayMode.NONE, "Clean View", Icons.Default.Wallpaper),
                        Triple(PreviewOverlayMode.LOCK_SCREEN, "Lock Screen", Icons.Default.PhoneAndroid),
                        Triple(PreviewOverlayMode.HOME_SCREEN, "Home Screen", Icons.Default.PhoneAndroid)
                    )

                    modes.forEach { (mode, label, _) ->
                        val isSelected = previewMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFFFFB800) else Color.Transparent)
                                .clickable { viewModel.setPreviewOverlay(mode) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0A0C10) else Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Action Buttons: Primary Apply + Secondary Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Export / Save to Gallery
                    Button(
                        onClick = { viewModel.exportToGallery(wallpaper) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E2433),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("export_gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Main Apply CTA
                    Button(
                        onClick = { showApplySheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB800),
                            contentColor = Color(0xFF0A0C10)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("apply_wallpaper_cta")
                    ) {
                        if (isApplying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF0A0C10),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Applying...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apply Wallpaper", fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Apply Wallpaper Sheet
        if (showApplySheet) {
            ModalBottomSheet(
                onDismissRequest = { showApplySheet = false },
                containerColor = Color(0xFF141822),
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Set as Wallpaper",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Select screen destination to apply high-resolution automotive background:",
                        fontSize = 12.sp,
                        color = Color(0xFFC5C9D3)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ApplyOptionCard(
                        title = "Home Screen",
                        subtitle = "Display on your device main launcher",
                        onClick = {
                            viewModel.applyWallpaper(WallpaperTarget.HOME_SCREEN)
                            showApplySheet = false
                        }
                    )

                    ApplyOptionCard(
                        title = "Lock Screen",
                        subtitle = "Display on lock screen behind time & notifications",
                        onClick = {
                            viewModel.applyWallpaper(WallpaperTarget.LOCK_SCREEN)
                            showApplySheet = false
                        }
                    )

                    ApplyOptionCard(
                        title = "Both Home & Lock Screen",
                        subtitle = "Unified luxury automotive aesthetic across your phone",
                        onClick = {
                            viewModel.applyWallpaper(WallpaperTarget.BOTH)
                            showApplySheet = false
                        },
                        isHighlight = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Car Specs & Prompt Sheet
        if (showSpecsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSpecsSheet = false },
                containerColor = Color(0xFF141822),
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vehicle & Mod Specification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        IconButton(
                            onClick = {
                                viewModel.deleteWallpaper(wallpaper)
                                showSpecsSheet = false
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF3366)
                            )
                        }
                    }

                    SpecRow("Platform", wallpaper.title)
                    SpecRow("Category", wallpaper.category)
                    SpecRow("Aero & Modifications", wallpaper.modificationStyle)
                    SpecRow("Resolution", wallpaper.resolution)
                    SpecRow("Aspect Ratio", wallpaper.aspectRatio)
                    SpecRow("AI Generated", if (wallpaper.isAiGenerated) "Yes (Custom AI Spec)" else "Curated Preset")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AI Design Prompt",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB800)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0A0C10))
                            .border(1.dp, Color(0xFF262D3D), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = wallpaper.prompt,
                            fontSize = 12.sp,
                            color = Color(0xFFC5C9D3),
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ApplyOptionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isHighlight: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isHighlight) Color(0xFFFFB800).copy(alpha = 0.15f) else Color(0xFF1E2433))
            .border(
                1.dp,
                if (isHighlight) Color(0xFFFFB800) else Color(0xFF2E374A),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) Color(0xFFFFB800) else Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFFC5C9D3)
                )
            }
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isHighlight) Color(0xFFFFB800) else Color(0xFFC5C9D3),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF818898))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
