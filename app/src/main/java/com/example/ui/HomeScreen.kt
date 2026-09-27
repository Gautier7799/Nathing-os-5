package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.LauncherClockStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.NosWidgetPortType
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import com.example.service.SystemPortHelper
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppIconItem
import com.example.ui.components.EnlargedFolderView
import com.example.ui.components.NosCalendarDigitalTimeWidget
import com.example.ui.components.NosCircularGaugesWidget
import com.example.ui.components.NosContactPillWidget
import com.example.ui.components.NosDecibelWidget
import com.example.ui.components.NosGiantCirclesClusterWidget
import com.example.ui.components.NosGlanceTextWidget
import com.example.ui.components.NosMiniClusterWidget
import com.example.ui.components.NosNothingXEarbudsWidget
import com.example.ui.components.NosQuickListWidget
import com.example.ui.components.NosStickerFocusClusterWidget
import com.example.ui.components.NosWidgetPortSheet
import com.example.ui.components.NothingAnalogClockWidget
import com.example.ui.components.ScalableWidget
import com.example.ui.components.NothingCassetteWidget
import com.example.ui.components.NothingClockWidget
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingQuickNoteWidget
import com.example.ui.components.NothingQuickTogglesWidget
import com.example.ui.components.NothingResourceWidget
import com.example.ui.components.NothingStepWidget
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.components.NothingWeatherWidget
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingWhite

@Composable
private fun HomeWidgetHost(
  widgetType: NosWidgetPortType,
  settings: LauncherSettings,
  accentColor: Color,
  onScaleChange: (NosWidgetPortType, Float) -> Unit,
  onReset: (NosWidgetPortType) -> Unit,
  onInfo: (NosWidgetPortType) -> Unit,
  content: @Composable () -> Unit
) {
  ScalableWidget(
    widgetType = widgetType,
    scale = settings.widgetScales[widgetType.name] ?: 1f,
    onScaleChange = { onScaleChange(widgetType, it) },
    onReset = { onReset(widgetType) },
    onWidgetInfo = { onInfo(widgetType) },
    accentColor = accentColor,
    content = content
  )
}

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
  onToggleThemeMode: () -> Unit = {},
  onToggleClockStyle: () -> Unit = {},
  onReorderPinnedApps: (Int, Int) -> Unit = { _, _ -> },
  onRemovePinnedApp: (AppItem) -> Unit = {},
  onToggleDockApp: (AppItem) -> Unit = {},
  onToggleWidget: (NosWidgetPortType) -> Unit = {},
  onOpenAppInfo: (AppItem) -> Unit = {},
  onSwipeUp: () -> Unit = {},
  isDrawerOpen: Boolean = false,
  onWidgetScaleChange: (NosWidgetPortType, Float) -> Unit = { _, _ -> },
  onResetWidgetScale: (NosWidgetPortType) -> Unit = {},
  onWidgetInfo: (NosWidgetPortType) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  val context = LocalContext.current
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  var isReorderingFavorites by remember { mutableStateOf(false) }
  var showWidgetSheet by remember { mutableStateOf(false) }
  val homeListState = rememberLazyListState()
  var topBarVisible by remember { mutableStateOf(true) }
  var dockVisible by remember { mutableStateOf(true) }

  // Hide the top controls and dock while moving down through widgets.
  // Restore them immediately when the user scrolls upward or returns to the top.
  LaunchedEffect(homeListState) {
    var previousPosition = 0
    snapshotFlow {
      homeListState.firstVisibleItemIndex * 100000 + homeListState.firstVisibleItemScrollOffset
    }.distinctUntilChanged().collectLatest { position ->
      if (position <= 8) {
        topBarVisible = true
        dockVisible = true
      } else if (position > previousPosition) {
        topBarVisible = false
        dockVisible = false
      } else if (position < previousPosition) {
        topBarVisible = true
        dockVisible = true
      }
      previousPosition = position
    }
  }


  Box(
    modifier = modifier
      .fillMaxSize()
      .blur(if (isDrawerOpen) 18.dp else 0.dp)
      .background(theme.background)
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background (Supports built-in & custom gallery photos)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = false,
      accentColor = accentColor,
      onDoubleTap = onDoubleTap
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Navigation / Glance Bar (With Swipe down for notifications)
      AnimatedVisibility(
        visible = topBarVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
      ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 12.dp)
                  .pointerInput(settings.swipeDownNotifications) {
                    if (settings.swipeDownNotifications) {
                      detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 70f) {
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
                    color = theme.textPrimary,
                    letterSpacing = 2.sp
                  )
                }
        
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  // Quick Day / Night Theme Toggle (Direct 1-tap switch between Image 3 Theme Jour and Image 2 Theme Nuit)
                  IconButton(
                    onClick = onToggleThemeMode,
                    modifier = Modifier.testTag("home_theme_toggle_button")
                  ) {
                    Icon(
                      imageVector = when (settings.themeMode) {
                        LauncherThemeMode.DARK -> Icons.Default.DarkMode
                        LauncherThemeMode.LIGHT -> Icons.Default.LightMode
                        LauncherThemeMode.RETRO_PASTEL -> Icons.Default.Palette
                        LauncherThemeMode.SYSTEM -> if (theme.isDark) Icons.Default.DarkMode else Icons.Default.LightMode
                      },
                      contentDescription = "Toggle Theme Jour / Nuit / Retro",
                      tint = if (settings.themeMode != LauncherThemeMode.DARK) accentColor else theme.textSecondary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
        
                  IconButton(
                    onClick = onDoubleTap,
                    modifier = Modifier.testTag("home_lock_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = "Lock Screen",
                      tint = theme.textSecondary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
        
                  IconButton(
                    onClick = { showWidgetSheet = true },
                    modifier = Modifier.testTag("home_widgets_port_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Widgets,
                      contentDescription = "NOS 3.5 Widgets Port",
                      tint = accentColor,
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
                      tint = theme.textSecondary
                    )
                  }
                }
              }
      }

      // Scrollable Home Screen Body (Widgets, Folders, Pinned Apps)
      LazyColumn(
        state = homeListState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Calendar & Digital Time Widget (Screenshot 2: JUL TUESDAY 07H 10M)
        if (settings.activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.CALENDAR_DIGITAL_TIME, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              NosCalendarDigitalTimeWidget(
                currentTime = currentTime,
                accentColor = accentColor,
                onCalendarClick = { SystemPortHelper.launchPixelCalendar(context) },
                onClockClick = { SystemPortHelper.launchPixelClock(context) }
              )
            }
          }
        }

        // 2. 2x2 Mini Cluster (Screenshot 2) + Analog Clock / Weather
        if (settings.activeWidgets.contains(NosWidgetPortType.MINI_CLUSTER_2X2)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.MINI_CLUSTER_2X2, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NosMiniClusterWidget(
                  weather = weather,
                  accentColor = accentColor,
                  onWeatherClick = { SystemPortHelper.launchPixelWeather(context) },
                  onHealthClick = { SystemPortHelper.launchHealthConnect(context) },
                  onRecorderClick = { SystemPortHelper.launchPixelClock(context) },
                  modifier = Modifier.weight(1f)
                )

                if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
                  val timeParts = currentTime.split(":")
                  val hours = timeParts.getOrNull(0) ?: "12"
                  val minutes = timeParts.getOrNull(1) ?: "00"
                  NothingAnalogClockWidget(
                    hours = hours,
                    minutes = minutes,
                    date = currentDate,
                    accentColor = accentColor,
                    onToggleStyle = onToggleClockStyle,
                    onOpenClockPort = { SystemPortHelper.launchPixelClock(context) },
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        } else if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
          // Signature Large Clock Widget (Dot Matrix or Round Analog)
          item {
            val timeParts = currentTime.split(":")
            val hours = timeParts.getOrNull(0) ?: "12"
            val minutes = timeParts.getOrNull(1) ?: "00"
            HomeWidgetHost(
              NosWidgetPortType.CLOCK_MAIN, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              if (settings.clockStyle == LauncherClockStyle.ANALOG) {
                NothingAnalogClockWidget(
                  hours = hours, minutes = minutes, date = currentDate, accentColor = accentColor,
                  onToggleStyle = onToggleClockStyle,
                  onOpenClockPort = { SystemPortHelper.launchPixelClock(context) }
                )
              } else {
                NothingClockWidget(
                  hours = hours, minutes = minutes, date = currentDate, accentColor = accentColor,
                  onToggleStyle = onToggleClockStyle,
                  onOpenClockPort = { SystemPortHelper.launchPixelClock(context) }
                )
              }
            }
          }
        }

        // 3. Text Glance Summary Widget (Screenshot 2: "TODAY IS TUESDAY AND TIME IS...")
        if (settings.activeWidgets.contains(NosWidgetPortType.GLANCE_TEXT_SUMMARY)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.GLANCE_TEXT_SUMMARY, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              NosGlanceTextWidget(
                currentTime = currentTime,
                weather = weather,
                batteryPct = toggles.batteryLevel,
                isCharging = toggles.isCharging,
                onGlanceClick = { SystemPortHelper.launchPixelWeather(context) }
              )
            }
          }
        }

        // 3.5. Giant Circles Cluster (Screenshot 3: Giant Camera, Rain Weather, Globe Disc)
        if (settings.activeWidgets.contains(NosWidgetPortType.GIANT_CIRCLES_CLUSTER)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.GIANT_CIRCLES_CLUSTER, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              NosGiantCirclesClusterWidget(
                weather = weather,
                currentTime = currentTime,
                accentColor = accentColor,
                onLaunchCamera = {
                  val camApp = AppItem("com.google.android.GoogleCamera", "", "Camera")
                  onAppClick(camApp)
                },
                onLaunchWeather = { SystemPortHelper.launchPixelWeather(context) }
              )
            }
          }
        }

        // 3.6. Sticker & Focus Cluster (Screenshot 5: Focus rings, Retro Car, Capsule)
        if (settings.activeWidgets.contains(NosWidgetPortType.STICKER_FOCUS_CLUSTER)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.STICKER_FOCUS_CLUSTER, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) { NosStickerFocusClusterWidget(accentColor = accentColor) }
          }
        }

        // 3.7. Nothing X Earbuds Widget (Screenshot 5: Headphones 90%, ANC mode)
        if (settings.activeWidgets.contains(NosWidgetPortType.NOTHING_X_EARBUDS)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.NOTHING_X_EARBUDS, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) { NosNothingXEarbudsWidget(accentColor = accentColor) }
          }
        }

        // 4. NOS 3.5 Circular Progress Gauges (Screenshot 1: Music 73%, Red Flame 57°C, Bell 98%)
        if (settings.activeWidgets.contains(NosWidgetPortType.CIRCULAR_GAUGES)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.CIRCULAR_GAUGES, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) { NosCircularGaugesWidget(accentColor = accentColor) }
          }
        }

        // 5. Decibel Sound Meter & Tasks Checklist (Screenshot 1)
        if (settings.activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER) ||
            settings.activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.DECIBEL_SOUND_METER, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                if (settings.activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER)) {
                  NosDecibelWidget(accentColor = accentColor, modifier = Modifier.weight(1f))
                }
                if (settings.activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST)) {
                  NosQuickListWidget(accentColor = accentColor, modifier = Modifier.weight(1.2f))
                }
              }
            }
          }
        }

        // 6. Favorite Contact Pill (Screenshot 1)
        if (settings.activeWidgets.contains(NosWidgetPortType.CONTACT_PILL)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.CONTACT_PILL, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              NosContactPillWidget(
                accentColor = accentColor,
                onCall = { SystemPortHelper.launchPixelClock(context) },
                onChat = { SystemPortHelper.launchPixelCalendar(context) }
              )
            }
          }
        }

        // 7. 2-Column Modular Widgets: Weather + Quick Toggles
        if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.WEATHER_MAIN, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NothingWeatherWidget(
                  weather = weather, onToggleCondition = onToggleWeather, accentColor = accentColor,
                  onOpenWeatherPort = { SystemPortHelper.launchPixelWeather(context) }, modifier = Modifier.weight(1f)
                )
                NothingQuickTogglesWidget(
                  toggles = toggles, onToggleTorch = onToggleTorch, onCycleSound = onCycleSound,
                  accentColor = accentColor, modifier = Modifier.weight(1.1f)
                )
              }
            }
          }
        }

        // 8. Teenage Cassette Retro Player
        if (settings.activeWidgets.contains(NosWidgetPortType.CASSETTE_PLAYER)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.CASSETTE_PLAYER, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              NothingCassetteWidget(
                audio = audio,
                onTogglePlay = onToggleAudioPlay,
                onNextTrack = onNextAudioTrack,
                accentColor = accentColor
              )
            }
          }
        }

        // 9. 2-Column Widgets: Pedometer & Storage/RAM
        if (settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)) {
          item {
            HomeWidgetHost(
              NosWidgetPortType.PEDOMETER_GAUGE, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NothingStepWidget(
                  fitness = fitness, onAddStep = onAddStep, accentColor = accentColor, modifier = Modifier.weight(1.1f)
                )
                NothingResourceWidget(
                  storagePct = storagePct, ramPct = ramPct, accentColor = accentColor, modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // 10. Quick Memo
        item {
          HomeWidgetHost(
            NosWidgetPortType.QUICK_CHECKLIST, settings, accentColor, onWidgetScaleChange, onResetWidgetScale, onWidgetInfo
          ) {
            NothingQuickNoteWidget(
              note = quickNote,
              onEditNote = onEditNote,
              accentColor = accentColor
            )
          }
        }

        // 11. Signature Nothing OS 2x2 Enlarged Folders
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

      }

      // Bottom Nothing Dock: hides while scrolling down through widgets and returns on upward scroll.
      AnimatedVisibility(
        visible = dockVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        NothingDock(
          dockApps = dockApps,
        onAppClick = onAppClick,
        onOpenDrawer = onOpenDrawer,
        onOpenSearch = onOpenDrawer,
        onOpenAppInfo = onOpenAppInfo,
        iconPack = settings.iconPack,
        accentColor = accentColor,
        // Search dock is intentionally removed from the launcher surface.
          showSearchBar = true
        )
      }
    }

    // NOS 3.5 Widgets Port Bottom Sheet Picker
    if (showWidgetSheet) {
      NosWidgetPortSheet(
        activeWidgets = settings.activeWidgets,
        onToggleWidget = onToggleWidget,
        onDismiss = { showWidgetSheet = false },
        accentColor = accentColor
      )
    }
  }
}