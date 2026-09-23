package com.example.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.IconPackStyle
import com.example.model.LauncherScreen
import com.example.model.LauncherSettings
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

  private val context: Context get() = getApplication<Application>().applicationContext

  // Screen navigation
  private val _currentScreen = MutableStateFlow(LauncherScreen.HOME)
  val currentScreen: StateFlow<LauncherScreen> = _currentScreen.asStateFlow()

  // App lists
  private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
  val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Home Screen items
  private val _pinnedApps = MutableStateFlow<List<AppItem>>(emptyList())
  val pinnedApps: StateFlow<List<AppItem>> = _pinnedApps.asStateFlow()

  private val _dockApps = MutableStateFlow<List<AppItem>>(emptyList())
  val dockApps: StateFlow<List<AppItem>> = _dockApps.asStateFlow()

  private val _folders = MutableStateFlow<List<FolderItem>>(emptyList())
  val folders: StateFlow<List<FolderItem>> = _folders.asStateFlow()

  // Time & Date
  private val _currentTime = MutableStateFlow("12:00")
  val currentTime: StateFlow<String> = _currentTime.asStateFlow()

  private val _currentSeconds = MutableStateFlow("00")
  val currentSeconds: StateFlow<String> = _currentSeconds.asStateFlow()

  private val _currentDate = MutableStateFlow("WED 23 SEP")
  val currentDate: StateFlow<String> = _currentDate.asStateFlow()

  // Weather
  private val _weather = MutableStateFlow(WeatherInfo(tempC = 23, condition = "SUNNY", city = "LONDON"))
  val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

  // Quick Toggles
  private val _toggles = MutableStateFlow(QuickToggleState())
  val toggles: StateFlow<QuickToggleState> = _toggles.asStateFlow()

  // Fitness & Audio
  private val _fitness = MutableStateFlow(FitnessStats())
  val fitness: StateFlow<FitnessStats> = _fitness.asStateFlow()

  private val _audio = MutableStateFlow(AudioState())
  val audio: StateFlow<AudioState> = _audio.asStateFlow()

  // Quick Note
  private val _quickNote = MutableStateFlow("NOTHING OS 5.0\n• Pure Minimalism\n• Zero Bloatware\n• Dot Matrix Engine")
  val quickNote: StateFlow<String> = _quickNote.asStateFlow()

  // System storage
  private val _storageUsedPercent = MutableStateFlow(48)
  val storageUsedPercent: StateFlow<Int> = _storageUsedPercent.asStateFlow()

  private val _ramUsedPercent = MutableStateFlow(62)
  val ramUsedPercent: StateFlow<Int> = _ramUsedPercent.asStateFlow()

  // Settings
  private val _settings = MutableStateFlow(LauncherSettings())
  val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

  // Active folder dialog
  private val _activeOpenFolder = MutableStateFlow<FolderItem?>(null)
  val activeOpenFolder: StateFlow<FolderItem?> = _activeOpenFolder.asStateFlow()

  // Battery receiver
  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.let { updateBatteryFromIntent(it) }
    }
  }

  init {
    loadInstalledApps()
    startClockUpdates()
    registerBatteryReceiver()
    checkSystemStorage()
  }

  fun setScreen(screen: LauncherScreen) {
    _currentScreen.value = screen
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun openFolder(folder: FolderItem?) {
    _activeOpenFolder.value = folder
  }

  fun toggleFolderEnlarged(folderId: String) {
    _folders.update { list ->
      list.map { if (it.id == folderId) it.copy(isEnlarged = !it.isEnlarged) else it }
    }
  }

  fun updateQuickNote(note: String) {
    _quickNote.value = note
  }

  fun updateSettings(newSettings: LauncherSettings) {
    _settings.value = newSettings
  }

  fun toggleAudioPlayback() {
    _audio.update { it.copy(isPlaying = !it.isPlaying) }
  }

  fun nextAudioTrack() {
    val tracks = listOf(
      "Nothing (R)" to "Tape Reel 01",
      "Glyph Pulse" to "Teenage Sound",
      "Monochrome Beats" to "Carl's Mix",
      "Swedish Engineering" to "Synthesizer Lab"
    )
    val currentIdx = tracks.indexOfFirst { it.first == _audio.value.title }
    val nextIdx = (currentIdx + 1) % tracks.size
    _audio.update {
      it.copy(
        title = tracks[nextIdx].first,
        artist = tracks[nextIdx].second,
        progress = 0f,
        isPlaying = true
      )
    }
  }

  fun toggleTorch() {
    val newState = !_toggles.value.isTorchOn
    try {
      val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
      val cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
        val chars = cameraManager.getCameraCharacteristics(id)
        chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
      }
      if (cameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cameraManager.setTorchMode(cameraId, newState)
      }
    } catch (_: Exception) {
      // Graceful fallback if camera torch is unavailable or in emulator
    }
    _toggles.update { it.copy(isTorchOn = newState) }
  }

  fun cycleSoundMode() {
    try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
      val currentMode = _toggles.value.soundMode
      val nextMode = (currentMode + 1) % 3 // 0: Silent, 1: Vibrate, 2: Normal
      audioManager?.let {
        when (nextMode) {
          0 -> it.ringerMode = AudioManager.RINGER_MODE_SILENT
          1 -> it.ringerMode = AudioManager.RINGER_MODE_VIBRATE
          2 -> it.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }
      }
      _toggles.update { it.copy(soundMode = nextMode) }
    } catch (_: Exception) {
      _toggles.update { it.copy(soundMode = (it.soundMode + 1) % 3) }
    }
  }

  fun toggleWeatherCondition() {
    val conditions = listOf("SUNNY", "CLOUDY", "RAIN", "THUNDER")
    val currentIdx = conditions.indexOf(_weather.value.condition)
    val nextIdx = (currentIdx + 1) % conditions.size
    val nextTemp = when (nextIdx) {
      0 -> 24
      1 -> 19
      2 -> 14
      else -> 17
    }
    _weather.update {
      it.copy(
        condition = conditions[nextIdx],
        tempC = nextTemp,
        highC = nextTemp + 3,
        lowC = nextTemp - 5
      )
    }
  }

  fun addSteps(amount: Int = 250) {
    _fitness.update {
      val newSteps = (it.steps + amount)
      it.copy(
        steps = newSteps,
        calories = (newSteps * 0.045f).toInt(),
        distanceKm = String.format(Locale.US, "%.1f", newSteps * 0.00075f).toFloat()
      )
    }
  }

  fun togglePinApp(app: AppItem) {
    _pinnedApps.update { current ->
      if (current.any { it.packageName == app.packageName }) {
        current.filterNot { it.packageName == app.packageName }
      } else {
        current + app.copy(isPinned = true)
      }
    }
  }

  fun toggleDockApp(app: AppItem) {
    _dockApps.update { current ->
      if (current.any { it.packageName == app.packageName }) {
        current.filterNot { it.packageName == app.packageName }
      } else if (current.size < 5) {
        current + app.copy(isDock = true)
      } else {
        current
      }
    }
  }

  fun launchApp(app: AppItem) {
    try {
      val pm = context.packageManager
      val intent = pm.getLaunchIntentForPackage(app.packageName)
      if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
      }
    } catch (_: Exception) {
      // Graceful catch for sandbox/preview
    }
  }

  fun openAppInfo(app: AppItem) {
    try {
      val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.parse("package:${app.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      // Fallback
    }
  }

  private fun loadInstalledApps() {
    viewModelScope.launch {
      try {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
          addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val loadedList = resolveInfos.mapNotNull { resolveInfo ->
          val pkg = resolveInfo.activityInfo.packageName
          if (pkg == context.packageName) return@mapNotNull null // Don't list launcher itself
          val label = resolveInfo.loadLabel(pm).toString()
          val icon = resolveInfo.loadIcon(pm)
          val category = when {
            label.contains("Camera", true) || label.contains("Photo", true) || label.contains("Gallery", true) -> "Media"
            label.contains("Message", true) || label.contains("Mail", true) || label.contains("Phone", true) || label.contains("Call", true) -> "Communication"
            label.contains("Setting", true) || label.contains("File", true) || label.contains("Clock", true) || label.contains("Calc", true) -> "Tools"
            else -> "General"
          }
          AppItem(
            packageName = pkg,
            activityName = resolveInfo.activityInfo.name,
            label = label,
            icon = icon,
            category = category
          )
        }.sortedBy { it.label.lowercase(Locale.ROOT) }

        // If real device has apps, use them; also ensure essential fallback apps exist
        val fallbackApps = getFallbackApps()
        val allApps = if (loadedList.isNotEmpty()) {
          // Merge fallback essentials if missing
          val existingPkgs = loadedList.map { it.packageName }.toSet()
          val missingEssentials = fallbackApps.filterNot { it.packageName in existingPkgs }
          (loadedList + missingEssentials).sortedBy { it.label.lowercase(Locale.ROOT) }
        } else {
          fallbackApps
        }

        _installedApps.value = allApps

        // Set default Dock Apps
        val dockList = allApps.filter { app ->
          app.label in listOf("Phone", "Messages", "Camera", "Chrome", "Browser")
        }.take(4).ifEmpty {
          allApps.take(4)
        }
        _dockApps.value = dockList

        // Set default Pinned Apps on Home
        val pinned = allApps.filterNot { it in dockList }.take(6)
        _pinnedApps.value = pinned

        // Create default Nothing OS signature folders
        val mediaApps = allApps.filter { it.category == "Media" || it.label in listOf("Camera", "Photos", "Gallery", "Music", "YouTube") }.take(4)
        val toolApps = allApps.filter { it.category == "Tools" || it.label in listOf("Settings", "Clock", "Calculator", "Files", "Notes") }.take(4)

        _folders.value = listOf(
          FolderItem(id = "folder_media", name = "MEDIA", isEnlarged = true, apps = mediaApps.ifEmpty { allApps.take(4) }),
          FolderItem(id = "folder_tools", name = "TOOLS", isEnlarged = true, apps = toolApps.ifEmpty { allApps.drop(4).take(4) })
        )
      } catch (e: Exception) {
        _installedApps.value = getFallbackApps()
      }
    }
  }

  private fun getFallbackApps(): List<AppItem> = listOf(
    AppItem("com.google.android.dialer", "", "Phone", null, category = "Communication"),
    AppItem("com.google.android.apps.messaging", "", "Messages", null, category = "Communication"),
    AppItem("com.android.chrome", "", "Chrome", null, category = "Tools"),
    AppItem("com.google.android.GoogleCamera", "", "Camera", null, category = "Media"),
    AppItem("com.google.android.apps.photos", "", "Photos", null, category = "Media"),
    AppItem("com.android.settings", "", "Settings", null, category = "Tools"),
    AppItem("com.google.android.deskclock", "", "Clock", null, category = "Tools"),
    AppItem("com.google.android.calculator", "", "Calculator", null, category = "Tools"),
    AppItem("com.google.android.apps.nbu.files", "", "Files", null, category = "Tools"),
    AppItem("com.google.android.keep", "", "Notes", null, category = "Tools"),
    AppItem("com.google.android.youtube", "", "YouTube", null, category = "Media"),
    AppItem("com.spotify.music", "", "Spotify", null, category = "Media"),
    AppItem("com.google.android.apps.maps", "", "Maps", null, category = "General"),
    AppItem("com.google.android.gm", "", "Gmail", null, category = "Communication"),
    AppItem("com.google.android.calendar", "", "Calendar", null, category = "Tools")
  )

  private fun startClockUpdates() {
    viewModelScope.launch {
      val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
      val secFormat = SimpleDateFormat("ss", Locale.getDefault())
      val dateFormat = SimpleDateFormat("EEE d MMM", Locale.US)

      while (true) {
        val now = Calendar.getInstance().time
        _currentTime.value = timeFormat.format(now)
        _currentSeconds.value = secFormat.format(now)
        _currentDate.value = dateFormat.format(now).uppercase(Locale.US)
        delay(1000)
      }
    }
  }

  private fun registerBatteryReceiver() {
    try {
      val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
      val batteryStatus: Intent? = context.registerReceiver(batteryReceiver, filter)
      batteryStatus?.let { updateBatteryFromIntent(it) }
    } catch (_: Exception) {
      // Fallback
    }
  }

  private fun updateBatteryFromIntent(intent: Intent) {
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
      status == BatteryManager.BATTERY_STATUS_FULL

    if (level >= 0 && scale > 0) {
      val batteryPct = (level * 100) / scale
      _toggles.update { it.copy(batteryLevel = batteryPct, isCharging = isCharging) }
    }
  }

  private fun checkSystemStorage() {
    try {
      val statFs = StatFs(Environment.getDataDirectory().path)
      val total = statFs.totalBytes
      val available = statFs.availableBytes
      if (total > 0) {
        val used = ((total - available) * 100 / total).toInt()
        _storageUsedPercent.value = used.coerceIn(10, 95)
      }
    } catch (_: Exception) {
      _storageUsedPercent.value = 42
    }
  }

  override fun onCleared() {
    super.onCleared()
    try {
      context.unregisterReceiver(batteryReceiver)
    } catch (_: Exception) {}
  }
}
