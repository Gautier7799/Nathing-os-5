package com.example.model

import android.graphics.drawable.Drawable

enum class IconPackStyle {
  MONOCHROME,      // Signature Nothing black/white circles with high contrast glyphs
  MINIMAL_DARK,    // Dark matte background with sleek white outlines
  SYSTEM_DEFAULT   // Original app icons
}

enum class LauncherScreen {
  HOME,
  APP_DRAWER,
  SETTINGS
}

data class AppItem(
  val packageName: String,
  val activityName: String = "",
  val label: String,
  val icon: Drawable? = null,
  val isPinned: Boolean = false,
  val isDock: Boolean = false,
  val category: String = "General"
)

data class FolderItem(
  val id: String,
  val name: String,
  val isEnlarged: Boolean = true, // Signature Nothing 2x2 enlarged folder
  val apps: List<AppItem> = emptyList()
)

data class WeatherInfo(
  val tempC: Int = 22,
  val condition: String = "SUNNY", // SUNNY, CLOUDY, RAIN, THUNDER, SNOW
  val city: String = "LONDON",
  val highC: Int = 25,
  val lowC: Int = 16
)

data class AudioState(
  val isPlaying: Boolean = false,
  val title: String = "Nothing (R)",
  val artist: String = "Tape Reel 01",
  val progress: Float = 0.42f
)

data class FitnessStats(
  val steps: Int = 7420,
  val goal: Int = 10000,
  val calories: Int = 345,
  val distanceKm: Float = 5.2f
)

data class QuickToggleState(
  val isTorchOn: Boolean = false,
  val soundMode: Int = 2, // 0 = Silent, 1 = Vibrate, 2 = Normal
  val batteryLevel: Int = 84,
  val isCharging: Boolean = false,
  val wifiEnabled: Boolean = true
)

data class LauncherSettings(
  val iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  val accentColorIndex: Int = 0, // 0: Red, 1: White, 2: Orange, 3: Yellow
  val gridColumns: Int = 4,
  val showLabels: Boolean = true,
  val is12HourFormat: Boolean = false,
  val tempUnitCelsius: Boolean = true
)
