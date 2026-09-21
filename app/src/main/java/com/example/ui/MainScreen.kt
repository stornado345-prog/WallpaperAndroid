package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SavedWallpapersScreen
import com.example.ui.screens.WallpaperDetailScreen

@Composable
fun MainScreen(
    viewModel: WallpaperViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val activeWallpaper by viewModel.activeWallpaper.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    if (activeWallpaper != null) {
        BackHandler {
            viewModel.closeWallpaperDetail()
        }
        WallpaperDetailScreen(
            wallpaper = activeWallpaper!!,
            viewModel = viewModel,
            onBack = { viewModel.closeWallpaperDetail() }
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 80.dp)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF10131A),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .border(1.dp, Color(0xFF222838), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Showcase"
                            )
                        },
                        label = {
                            Text(
                                "Showcase",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0A0C10),
                            selectedTextColor = Color(0xFFFFB800),
                            indicatorColor = Color(0xFFFFB800),
                            unselectedIconColor = Color(0xFF818898),
                            unselectedTextColor = Color(0xFF818898)
                        ),
                        modifier = Modifier.testTag("tab_showcase")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "AI Studio"
                            )
                        },
                        label = {
                            Text(
                                "AI Studio",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0A0C10),
                            selectedTextColor = Color(0xFFFFB800),
                            indicatorColor = Color(0xFFFFB800),
                            unselectedIconColor = Color(0xFF818898),
                            unselectedTextColor = Color(0xFF818898)
                        ),
                        modifier = Modifier.testTag("tab_ai_studio")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
                                contentDescription = "Garage"
                            )
                        },
                        label = {
                            Text(
                                "Garage",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0A0C10),
                            selectedTextColor = Color(0xFFFFB800),
                            indicatorColor = Color(0xFFFFB800),
                            unselectedIconColor = Color(0xFF818898),
                            unselectedTextColor = Color(0xFF818898)
                        ),
                        modifier = Modifier.testTag("tab_garage")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { targetIndex ->
                    when (targetIndex) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToGenerator = { selectedTab = 1 }
                        )
                        1 -> GeneratorScreen(
                            viewModel = viewModel
                        )
                        2 -> SavedWallpapersScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
