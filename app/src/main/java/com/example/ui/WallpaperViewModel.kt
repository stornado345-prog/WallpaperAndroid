package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiWallpaperService
import com.example.data.AppDatabase
import com.example.data.WallpaperEntity
import com.example.data.WallpaperRepository
import com.example.util.CustomModSpec
import com.example.util.WallpaperHelper
import com.example.util.WallpaperRenderer
import com.example.util.WallpaperTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PreviewOverlayMode {
    NONE,
    LOCK_SCREEN,
    HOME_SCREEN
}

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WallpaperRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WallpaperRepository(db.wallpaperDao(), application)
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val allWallpapers: StateFlow<List<WallpaperEntity>> = repository.allWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteWallpapers: StateFlow<List<WallpaperEntity>> = repository.favoriteWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    val filteredWallpapers: StateFlow<List<WallpaperEntity>> = combine(
        allWallpapers,
        categoryFilter
    ) { list, category ->
        if (category == "All") list
        else if (category == "Favorites") list.filter { it.isFavorite }
        else list.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generator State
    private val _selectedCar = MutableStateFlow("Lamborghini Aventador SVJ")
    val selectedCar = _selectedCar.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Hypercar")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedModStyle = MutableStateFlow("Forged Carbon Aero")
    val selectedModStyle = _selectedModStyle.asStateFlow()

    private val _selectedPaint = MutableStateFlow("Matte Obsidian Black")
    val selectedPaint = _selectedPaint.asStateFlow()

    private val _selectedWheels = MutableStateFlow("Forged Rose Gold Rims")
    val selectedWheels = _selectedWheels.asStateFlow()

    private val _selectedEnvironment = MutableStateFlow("Rain-Slicked Tokyo Neon")
    val selectedEnvironment = _selectedEnvironment.asStateFlow()

    private val _customPrompt = MutableStateFlow("")
    val customPrompt = _customPrompt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _isEnhancing = MutableStateFlow(false)
    val isEnhancing = _isEnhancing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    // Preview / Detail Screen State
    private val _activeWallpaper = MutableStateFlow<WallpaperEntity?>(null)
    val activeWallpaper = _activeWallpaper.asStateFlow()

    private val _previewOverlay = MutableStateFlow(PreviewOverlayMode.NONE)
    val previewOverlay = _previewOverlay.asStateFlow()

    private val _isApplying = MutableStateFlow(false)
    val isApplying = _isApplying.asStateFlow()

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun setSelectedCar(car: String) {
        _selectedCar.value = car
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSelectedModStyle(style: String) {
        _selectedModStyle.value = style
    }

    fun setSelectedPaint(paint: String) {
        _selectedPaint.value = paint
    }

    fun setSelectedWheels(wheels: String) {
        _selectedWheels.value = wheels
    }

    fun setSelectedEnvironment(env: String) {
        _selectedEnvironment.value = env
    }

    fun setCustomPrompt(prompt: String) {
        _customPrompt.value = prompt
    }

    fun setPreviewOverlay(mode: PreviewOverlayMode) {
        _previewOverlay.value = mode
    }

    fun openWallpaperDetail(wallpaper: WallpaperEntity) {
        _activeWallpaper.value = wallpaper
        _previewOverlay.value = PreviewOverlayMode.NONE
    }

    fun closeWallpaperDetail() {
        _activeWallpaper.value = null
        _previewOverlay.value = PreviewOverlayMode.NONE
    }

    fun toggleFavorite(wallpaper: WallpaperEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaper)
            if (_activeWallpaper.value?.id == wallpaper.id) {
                _activeWallpaper.value = _activeWallpaper.value?.copy(isFavorite = !wallpaper.isFavorite)
            }
        }
    }

    fun deleteWallpaper(wallpaper: WallpaperEntity) {
        viewModelScope.launch {
            repository.deleteWallpaper(wallpaper)
            if (_activeWallpaper.value?.id == wallpaper.id) {
                _activeWallpaper.value = null
            }
            _statusMessage.value = "Wallpaper removed from your garage"
        }
    }

    fun enhancePrompt() {
        viewModelScope.launch {
            _isEnhancing.value = true
            val baseIdea = _customPrompt.value.ifBlank {
                "${_selectedPaint.value} ${_selectedCar.value}"
            }
            val enhanced = GeminiWallpaperService.enhancePrompt(
                userIdea = baseIdea,
                category = _selectedCategory.value,
                modStyle = _selectedModStyle.value,
                environment = _selectedEnvironment.value,
                wheels = _selectedWheels.value
            )
            _customPrompt.value = enhanced
            _isEnhancing.value = false
            _statusMessage.value = "Prompt elevated by AI design engine"
        }
    }

    fun generateWallpaper() {
        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = null

            val title = "${_selectedCar.value} • ${_selectedModStyle.value}"
            val finalPrompt = _customPrompt.value.ifBlank {
                "${_selectedPaint.value} ${_selectedCar.value} with bespoke ${_selectedModStyle.value}, ${_selectedWheels.value}, in ${_selectedEnvironment.value}, vertical 8k wallpaper"
            }

            var generatedBitmap: Bitmap? = null

            // Attempt direct Gemini generation if API key is present
            if (GeminiWallpaperService.hasApiKey()) {
                val apiResult = GeminiWallpaperService.generateWallpaper(finalPrompt, "9:16")
                apiResult.onSuccess { bitmap ->
                    generatedBitmap = bitmap
                }.onFailure {
                    // Fall back to high-res custom renderer
                    _statusMessage.value = "Rendering luxury mod specification..."
                }
            }

            // Fallback / Procedural High-Res Render Engine
            if (generatedBitmap == null) {
                val spec = CustomModSpec(
                    carModel = _selectedCar.value,
                    category = _selectedCategory.value,
                    modStyle = _selectedModStyle.value,
                    paintColor = _selectedPaint.value,
                    wheelType = _selectedWheels.value,
                    environment = _selectedEnvironment.value,
                    customPrompt = finalPrompt
                )
                generatedBitmap = WallpaperRenderer.renderCustomModifiedWallpaper(getApplication(), spec)
            }

            // Save to offline internal storage
            val fileName = "custom_mod_${System.currentTimeMillis()}"
            val saveResult = WallpaperHelper.saveBitmapToInternalStorage(
                getApplication(),
                generatedBitmap,
                fileName
            )

            val filePath = saveResult.getOrNull()?.absolutePath

            val entity = WallpaperEntity(
                title = title,
                prompt = finalPrompt,
                category = _selectedCategory.value,
                modificationStyle = _selectedModStyle.value,
                filePath = filePath,
                drawableResName = null,
                isFavorite = false,
                resolution = "1080x1920 (Ultra HD)",
                aspectRatio = "9:16",
                isAiGenerated = true,
                createdAt = System.currentTimeMillis()
            )

            val newId = repository.insertWallpaper(entity)
            val savedEntity = entity.copy(id = newId)

            _isGenerating.value = false
            _activeWallpaper.value = savedEntity
            _statusMessage.value = "Wallpaper generated & saved offline!"
        }
    }

    fun applyWallpaper(target: WallpaperTarget) {
        val wallpaper = _activeWallpaper.value ?: return
        viewModelScope.launch {
            _isApplying.value = true
            val context = getApplication<Application>()
            val bitmap = WallpaperHelper.loadBitmap(context, wallpaper.filePath, wallpaper.drawableResName)

            if (bitmap != null) {
                val result = WallpaperHelper.applyWallpaper(context, bitmap, target)
                _isApplying.value = false
                if (result.isSuccess) {
                    val targetName = when (target) {
                        WallpaperTarget.HOME_SCREEN -> "Home Screen"
                        WallpaperTarget.LOCK_SCREEN -> "Lock Screen"
                        WallpaperTarget.BOTH -> "Home & Lock Screens"
                    }
                    _statusMessage.value = "Wallpaper applied to $targetName!"
                } else {
                    _statusMessage.value = "Failed to apply wallpaper: ${result.exceptionOrNull()?.localizedMessage}"
                }
            } else {
                _isApplying.value = false
                _statusMessage.value = "Could not load wallpaper image"
            }
        }
    }

    fun exportToGallery(wallpaper: WallpaperEntity) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val bitmap = WallpaperHelper.loadBitmap(context, wallpaper.filePath, wallpaper.drawableResName)
            if (bitmap != null) {
                val result = WallpaperHelper.exportToGallery(context, bitmap)
                if (result.isSuccess) {
                    _statusMessage.value = "Saved to Pictures/ApexCraft Gallery!"
                } else {
                    _statusMessage.value = "Export failed: ${result.exceptionOrNull()?.localizedMessage}"
                }
            }
        }
    }

    fun shareWallpaper(wallpaper: WallpaperEntity) {
        val context = getApplication<Application>()
        val bitmap = WallpaperHelper.loadBitmap(context, wallpaper.filePath, wallpaper.drawableResName)
        if (bitmap != null) {
            WallpaperHelper.shareWallpaper(context, bitmap, wallpaper.title)
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
