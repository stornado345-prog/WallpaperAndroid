package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.WallpaperViewModel
import com.example.ui.components.PresetChipRow

@Composable
fun GeneratorScreen(
    viewModel: WallpaperViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCar by viewModel.selectedCar.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedModStyle by viewModel.selectedModStyle.collectAsStateWithLifecycle()
    val selectedPaint by viewModel.selectedPaint.collectAsStateWithLifecycle()
    val selectedWheels by viewModel.selectedWheels.collectAsStateWithLifecycle()
    val selectedEnv by viewModel.selectedEnvironment.collectAsStateWithLifecycle()
    val customPrompt by viewModel.customPrompt.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isEnhancing by viewModel.isEnhancing.collectAsStateWithLifecycle()

    val cars = listOf(
        "Lamborghini Aventador SVJ",
        "Porsche 911 GT3 RS",
        "Nissan GT-R Nismo",
        "Rolls-Royce Phantom",
        "Ferrari SF90 XX",
        "McLaren 765LT",
        "BMW M4 CSL"
    )

    val modStyles = listOf(
        "Forged Carbon Aero",
        "Liberty Walk Widebody",
        "VIP Stanced Flush",
        "Cyber Midnight Glow",
        "Track Weissach Spec",
        "Matte Stealth Armor"
    )

    val paints = listOf(
        "Matte Obsidian Black",
        "Midnight Purple Chameleon",
        "Frozen Amber Gold",
        "Rosso Corsa Red",
        "Acid Green Neon",
        "Satin Titanium Silver"
    )

    val wheels = listOf(
        "Forged Rose Gold Rims",
        "Carbon Turbofan Discs",
        "Deep Dish Chrome",
        "Matte Bronze Multi-Spoke"
    )

    val environments = listOf(
        "Rain-Slicked Tokyo Neon",
        "Neon Cyber Workshop",
        "Monaco Coastal Sunset",
        "Dark Lightbox Studio",
        "Alpine Fog Mountain Pass"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 36.dp)
    ) {
        // Studio Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF261D0A), Color(0xFF0A0C10))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB800).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFFB800), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI MODIFICATION STUDIO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB800),
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Generate Custom Wallpaper",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure supercar aerodynamics, forged wheel stance, bespoke paintwork, and photo-studio lighting. AI generates high-resolution 8K outputs with seamless offline persistence.",
                    fontSize = 12.sp,
                    color = Color(0xFFC5C9D3),
                    lineHeight = 16.sp
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Vehicle Selection
            PresetChipRow(
                title = "1. Supercar Base Platform",
                items = cars,
                selectedItem = selectedCar,
                onItemSelected = { car ->
                    viewModel.setSelectedCar(car)
                    val cat = when {
                        car.contains("Phantom") -> "VIP Luxury"
                        car.contains("Porsche") || car.contains("SF90") -> "Track Weapon"
                        car.contains("GT-R") || car.contains("M4") -> "Super GT"
                        else -> "Hypercar"
                    }
                    viewModel.setSelectedCategory(cat)
                },
                colorAccent = Color(0xFFFFB800)
            )

            // 2. Mod Style
            PresetChipRow(
                title = "2. Luxury Modification Kit",
                items = modStyles,
                selectedItem = selectedModStyle,
                onItemSelected = { viewModel.setSelectedModStyle(it) },
                colorAccent = Color(0xFF00E5FF)
            )

            // 3. Paint & Finish
            PresetChipRow(
                title = "3. Bespoke Paint & Finish",
                items = paints,
                selectedItem = selectedPaint,
                onItemSelected = { viewModel.setSelectedPaint(it) },
                colorAccent = Color(0xFFFF3366)
            )

            // 4. Wheels & Fitment
            PresetChipRow(
                title = "4. Forged Wheels & Stance",
                items = wheels,
                selectedItem = selectedWheels,
                onItemSelected = { viewModel.setSelectedWheels(it) },
                colorAccent = Color(0xFFFFD566)
            )

            // 5. Scene & Lighting
            PresetChipRow(
                title = "5. Environment & Lighting",
                items = environments,
                selectedItem = selectedEnv,
                onItemSelected = { viewModel.setSelectedEnvironment(it) },
                colorAccent = Color(0xFF00E5FF)
            )

            // 6. Custom Prompt Input & AI Enhancer
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "6. Custom Spec / Prompt (Optional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Button(
                        onClick = { viewModel.enhancePrompt() },
                        enabled = !isEnhancing && !isGenerating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E2433),
                            contentColor = Color(0xFF00E5FF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("ai_enhance_prompt_button")
                    ) {
                        if (isEnhancing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF00E5FF),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Enhance",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customPrompt,
                    onValueChange = { viewModel.setCustomPrompt(it) },
                    placeholder = {
                        Text(
                            "Add specific aero wings, exhaust flames, titanium roll cage, gold calipers...",
                            fontSize = 12.sp,
                            color = Color(0xFF818898)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_prompt_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141822),
                        unfocusedContainerColor = Color(0xFF141822),
                        focusedBorderColor = Color(0xFFFFB800),
                        unfocusedBorderColor = Color(0xFF262D3D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    minLines = 2,
                    maxLines = 4
                )
            }

            // Specs Summary Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF141822))
                    .border(1.dp, Color(0xFF262D3D), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SPEC SUMMARY • 1080x1920 8K RENDERING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB800)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$selectedCar | $selectedModStyle | $selectedPaint | $selectedWheels | $selectedEnv",
                        fontSize = 12.sp,
                        color = Color(0xFFC5C9D3),
                        lineHeight = 16.sp
                    )
                }
            }

            // Primary Generate Button
            Button(
                onClick = { viewModel.generateWallpaper() },
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("generate_wallpaper_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB800),
                    contentColor = Color(0xFF0A0C10),
                    disabledContainerColor = Color(0xFF332900),
                    disabledContentColor = Color(0xFF886A00)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color(0xFF0A0C10),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Generating High-Res Wallpaper...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate 8K Luxury Wallpaper",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
