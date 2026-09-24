package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
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
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.components.NothingWeatherWidget
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
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
  onReorderPinnedApps: (Int, Int) -> Unit = { _, _ -> },
  onRemovePinnedApp: (AppItem) -> Unit = {},
  onToggleDockApp: (AppItem) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  var isReorderingFavorites by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingBlack)
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background (Supports built-in & custom gallery photos)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = false,
      accentColor = accentColor,
      onDoubleTap = onDoubleTap
    )

    Column(modifier = Modifier.fillMaxSize()) {
      // Top Navigation / Glance Bar (With Swipe down for notifications)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .pointerInput(settings.swipeDownNotifications) {
            if (settings.swipeDownNotifications) {
              detectVerticalDragGestures { _, dragAmount ->
                if (dragAmount > 30f) {
                  onSwipeDown()
                }
              }
            }
          },
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

        // 7. Pinned Apps on Home (With Full Touch Reordering & Quick Controls)
        if (pinnedApps.isNotEmpty()) {
          item {
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(accentColor)
                  )
                  Text(
                    text = "FAVORITES",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingWhite,
                    letterSpacing = 1.sp
                  )
                }

                // Rearrange / Move Mode Toggle Pill
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isReorderingFavorites) accentColor else NothingDarkSurface)
                    .border(1.dp, if (isReorderingFavorites) accentColor else NothingBorder, RoundedCornerShape(12.dp))
                    .clickable { isReorderingFavorites = !isReorderingFavorites }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("rearrange_favorites_button")
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.SwapHoriz,
                      contentDescription = null,
                      tint = if (isReorderingFavorites) NothingBlack else accentColor,
                      modifier = Modifier.size(13.dp)
                    )
                    Text(
                      text = if (isReorderingFavorites) "DONE" else "REARRANGE",
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isReorderingFavorites) NothingBlack else NothingWhite,
                      letterSpacing = 1.sp
                    )
                  }
                }
              }

              // Non-nested clean grid using chunked Rows
              val chunkedApps = pinnedApps.chunked(settings.gridColumns)
              chunkedApps.forEachIndexed { rowIndex, rowApps ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                  rowApps.forEachIndexed { colIndex, app ->
                    val actualIndex = rowIndex * settings.gridColumns + colIndex
                    var itemDragOffset by remember(app.packageName) { mutableFloatStateOf(0f) }

                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .offset { IntOffset(itemDragOffset.roundToInt(), 0) }
                        .scale(if (isReorderingFavorites) 1.03f else 1f)
                        .then(
                          if (isReorderingFavorites) {
                            Modifier
                              .background(NothingDarkSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                              .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                              .pointerInput(app.packageName) {
                                detectHorizontalDragGestures(
                                  onHorizontalDrag = { _, dragAmount ->
                                    itemDragOffset += dragAmount
                                  },
                                  onDragEnd = {
                                    if (itemDragOffset > 40f && actualIndex < pinnedApps.size - 1) {
                                      onReorderPinnedApps(actualIndex, actualIndex + 1)
                                    } else if (itemDragOffset < -40f && actualIndex > 0) {
                                      onReorderPinnedApps(actualIndex, actualIndex - 1)
                                    }
                                    itemDragOffset = 0f
                                  },
                                  onDragCancel = {
                                    itemDragOffset = 0f
                                  }
                                )
                              }
                          } else Modifier
                        )
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        AppIconItem(
                          app = app,
                          onClick = {
                            if (!isReorderingFavorites) {
                              onAppClick(app)
                            }
                          },
                          onLongClick = {
                            isReorderingFavorites = true
                          },
                          iconSize = 52.dp,
                          showLabel = settings.showLabels,
                          iconPack = settings.iconPack,
                          accentColor = accentColor
                        )

                        // Quick Rearrange Touch Controls when active
                        if (isReorderingFavorites) {
                          Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            if (actualIndex > 0) {
                              Box(
                                modifier = Modifier
                                  .size(24.dp)
                                  .clip(CircleShape)
                                  .background(NothingElevated)
                                  .clickable { onReorderPinnedApps(actualIndex, actualIndex - 1) },
                                contentAlignment = Alignment.Center
                              ) {
                                Icon(
                                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                  contentDescription = "Move Left",
                                  tint = NothingWhite,
                                  modifier = Modifier.size(14.dp)
                                )
                              }
                            }

                            if (actualIndex < pinnedApps.size - 1) {
                              Box(
                                modifier = Modifier
                                  .size(24.dp)
                                  .clip(CircleShape)
                                  .background(NothingElevated)
                                  .clickable { onReorderPinnedApps(actualIndex, actualIndex + 1) },
                                contentAlignment = Alignment.Center
                              ) {
                                Icon(
                                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                  contentDescription = "Move Right",
                                  tint = NothingWhite,
                                  modifier = Modifier.size(14.dp)
                                )
                              }
                            }

                            Box(
                              modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NothingElevated)
                                .clickable { onRemovePinnedApp(app) },
                              contentAlignment = Alignment.Center
                            ) {
                              Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                              )
                            }
                          }
                        }
                      }
                    }
                  }

                  // Pad with empty weights if row is incomplete
                  val missingInRow = settings.gridColumns - rowApps.size
                  repeat(missingInRow) {
                    Spacer(modifier = Modifier.weight(1f))
                  }
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
