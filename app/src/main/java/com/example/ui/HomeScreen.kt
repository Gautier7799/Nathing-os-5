package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.LauncherSettings
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppIconItem
import com.example.ui.components.EnlargedFolderView
import com.example.ui.components.NothingCassetteWidget
import com.example.ui.components.NothingClockWidget
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingQuickNoteWidget
import com.example.ui.components.NothingQuickTogglesWidget
import com.example.ui.components.NothingResourceWidget
import com.example.ui.components.NothingStepWidget
import com.example.ui.components.NothingWeatherWidget
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingWhite

@Composable
fun HomeScreen(
  currentTime: String,
  currentDate: String,
  weather: WeatherInfo,
  toggles: QuickToggleState,
  fitness: FitnessStats,
  audio: AudioState,
  quickNote: String,
  storagePct: Int,
  ramPct: Int,
  folders: List<FolderItem>,
  pinnedApps: List<AppItem>,
  dockApps: List<AppItem>,
  settings: LauncherSettings,
  onAppClick: (AppItem) -> Unit,
  onOpenFolder: (FolderItem) -> Unit,
  onToggleFolderEnlarged: (String) -> Unit,
  onToggleTorch: () -> Unit,
  onCycleSound: () -> Unit,
  onToggleWeather: () -> Unit,
  onAddStep: () -> Unit,
  onToggleAudioPlay: () -> Unit,
  onNextAudioTrack: () -> Unit,
  onEditNote: () -> Unit,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onSwipeDown: () -> Unit = {},
  onDoubleTap: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  var dragOffsetY by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingBlack)
      .pointerInput(settings.doubleTapToSleep) {
        if (settings.doubleTapToSleep) {
          detectTapGestures(
            onDoubleTap = { onDoubleTap() }
          )
        }
      }
      .pointerInput(settings.swipeDownNotifications) {
        detectVerticalDragGestures(
          onVerticalDrag = { _, dragAmount ->
            dragOffsetY += dragAmount
          },
          onDragEnd = {
            if (dragOffsetY < -60f) {
              // Upward swipe -> Open App Drawer
              onOpenDrawer()
            } else if (dragOffsetY > 60f && settings.swipeDownNotifications) {
              // Downward swipe -> Open Notifications Panel
              onSwipeDown()
            }
            dragOffsetY = 0f
          }
        )
      }
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background
    when (settings.wallpaperIndex) {
      0 -> {
        // Dot Matrix Noir
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.06f)) {
          val dotSpacing = 32f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }
      1 -> {
        // Pure Carbon Matte (Clean deep black)
      }
      2 -> {
        // Red Circuit Glow
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.12f)) {
          drawLine(accentColor, Offset(0f, size.height * 0.25f), Offset(size.width * 0.4f, size.height * 0.25f), strokeWidth = 2f)
          drawLine(accentColor, Offset(size.width * 0.4f, size.height * 0.25f), Offset(size.width * 0.65f, size.height * 0.4f), strokeWidth = 2f)
          drawLine(accentColor, Offset(size.width * 0.65f, size.height * 0.4f), Offset(size.width, size.height * 0.4f), strokeWidth = 2f)
          drawCircle(accentColor, 4.5f, Offset(size.width * 0.4f, size.height * 0.25f))
          drawCircle(accentColor, 4.5f, Offset(size.width * 0.65f, size.height * 0.4f))
        }
      }
      3 -> {
        // Light Monochrome Matrix
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.14f)) {
          val dotSpacing = 24f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.4f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }
    }

    Column(modifier = Modifier.fillMaxSize()) {
      // Top Navigation / Glance Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = "NOTHING",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = NothingWhite,
            letterSpacing = 2.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          IconButton(
            onClick = onDoubleTap,
            modifier = Modifier.testTag("home_lock_button")
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Lock Screen",
              tint = NothingGrey,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.testTag("home_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Launcher Settings",
              tint = NothingGrey
            )
          }
        }
      }

      // Scrollable Home Screen Body (Widgets, Folders, Pinned Apps)
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Signature Large Dot Clock Widget
        item {
          val timeParts = currentTime.split(":")
          val hours = timeParts.getOrNull(0) ?: "12"
          val minutes = timeParts.getOrNull(1) ?: "00"
          NothingClockWidget(
            hours = hours,
            minutes = minutes,
            date = currentDate,
            accentColor = accentColor
          )
        }

        // 2. 2-Column Modular Widgets: Weather + Quick Toggles
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            NothingWeatherWidget(
              weather = weather,
              onToggleCondition = onToggleWeather,
              accentColor = accentColor,
              modifier = Modifier.weight(1f)
            )

            NothingQuickTogglesWidget(
              toggles = toggles,
              onToggleTorch = onToggleTorch,
              onCycleSound = onCycleSound,
              accentColor = accentColor,
              modifier = Modifier.weight(1.1f)
            )
          }
        }

        // 3. Teenage Cassette Retro Player
        item {
          NothingCassetteWidget(
            audio = audio,
            onTogglePlay = onToggleAudioPlay,
            onNextTrack = onNextAudioTrack,
            accentColor = accentColor
          )
        }

        // 4. 2-Column Widgets: Pedometer & Storage/RAM
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            NothingStepWidget(
              fitness = fitness,
              onAddStep = onAddStep,
              accentColor = accentColor,
              modifier = Modifier.weight(1.1f)
            )

            NothingResourceWidget(
              storagePct = storagePct,
              ramPct = ramPct,
              accentColor = accentColor,
              modifier = Modifier.weight(1f)
            )
          }
        }

        // 5. Quick Memo
        item {
          NothingQuickNoteWidget(
            note = quickNote,
            onEditNote = onEditNote,
            accentColor = accentColor
          )
        }

        // 6. Signature Nothing OS 2x2 Enlarged Folders
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            folders.forEach { folder ->
              EnlargedFolderView(
                folder = folder,
                onAppClick = onAppClick,
                onOpenFolderSheet = { onOpenFolder(folder) },
                onToggleEnlarged = { onToggleFolderEnlarged(folder.id) },
                iconPack = settings.iconPack,
                accentColor = accentColor,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // 7. Pinned Apps on Home
        if (pinnedApps.isNotEmpty()) {
          item {
            Column {
              Text(
                text = "FAVORITES",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NothingGrey,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
              )

              LazyVerticalGrid(
                columns = GridCells.Fixed(settings.gridColumns),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(((pinnedApps.size + settings.gridColumns - 1) / settings.gridColumns * 86).dp)
              ) {
                items(pinnedApps, key = { it.packageName }) { app ->
                  AppIconItem(
                    app = app,
                    onClick = { onAppClick(app) },
                    iconSize = 50.dp,
                    showLabel = settings.showLabels,
                    iconPack = settings.iconPack,
                    accentColor = accentColor
                  )
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // Bottom Persistent Nothing Dock & Search
      NothingDock(
        dockApps = dockApps,
        onAppClick = onAppClick,
        onOpenDrawer = onOpenDrawer,
        onOpenSearch = onOpenDrawer,
        iconPack = settings.iconPack,
        accentColor = accentColor,
        showSearchBar = settings.showSearchBarOnDock
      )
    }
  }
}
