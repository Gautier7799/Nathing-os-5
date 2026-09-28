package com.example

import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LauncherClockStyle
import com.example.model.LauncherScreen
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.LockShortcutType
import com.example.ui.HomeScreen
import com.example.ui.NothingLockScreen
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppDrawerSheet
import com.example.ui.components.AppOptionsSheet
import com.example.ui.components.EditNoteDialog
import com.example.ui.components.ExpandedFolderSheet
import com.example.ui.components.LauncherSettingsDialog
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LauncherViewModel by viewModels()

  private val roleRequestLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      // Android will call us again after the Home role dialog. No further action
      // is required; the activity is already the Home entry point.
    }

  private val keyguardReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_USER_PRESENT &&
          viewModel.settings.value.lockScreen.isLockScreenEnabled) {
        viewModel.lockLauncherScreen()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
    registerReceiver(keyguardReceiver, filter)

    setContent {
      val settings by viewModel.settings.collectAsStateWithLifecycle()
      MyApplicationTheme(themeMode = settings.themeMode) {
        val theme = LocalLauncherTheme.current
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = theme.background,
          contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
          NothingLauncherApp(
            viewModel = viewModel,
            settings = settings,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }

    requestHomeRoleIfNeeded()
  }

  override fun onResume() {
    super.onResume()
    // Re-check when returning from Settings/Home-role UI.
    requestHomeRoleIfNeeded()
  }

  private fun requestHomeRoleIfNeeded() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
    val roleManager = getSystemService(RoleManager::class.java) ?: return
    if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME) &&
        roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
      roleRequestLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
    }
  }

  override fun onDestroy() {
    try {
      unregisterReceiver(keyguardReceiver)
    } catch (_: Exception) {
    }
    super.onDestroy()
  }
}

@Composable
fun NothingLauncherApp(
  viewModel: LauncherViewModel,
  settings: LauncherSettings,
  modifier: Modifier = Modifier
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
  val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val toggles by viewModel.toggles.collectAsStateWithLifecycle()
  val fitness by viewModel.fitness.collectAsStateWithLifecycle()
  val audio by viewModel.audio.collectAsStateWithLifecycle()
  val quickNote by viewModel.quickNote.collectAsStateWithLifecycle()
  val storagePct by viewModel.storageUsedPercent.collectAsStateWithLifecycle()
  val ramPct by viewModel.ramUsedPercent.collectAsStateWithLifecycle()
  val folders by viewModel.folders.collectAsStateWithLifecycle()
  val pinnedApps by viewModel.pinnedApps.collectAsStateWithLifecycle()
  val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
  val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val activeFolder by viewModel.activeOpenFolder.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()

  var isEditingNote by remember { mutableStateOf(false) }
  var isSettingsOpen by remember { mutableStateOf(false) }
  var selectedAppForOptions by remember { mutableStateOf<com.example.model.AppItem?>(null) }

  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  BackHandler(enabled = currentScreen == LauncherScreen.APP_DRAWER ||
      currentScreen == LauncherScreen.LOCK_SCREEN ||
      isSettingsOpen || activeFolder != null) {
    if (currentScreen == LauncherScreen.LOCK_SCREEN) {
    } else if (activeFolder != null) {
      viewModel.openFolder(null)
    } else if (isSettingsOpen) {
      isSettingsOpen = false
    } else if (currentScreen == LauncherScreen.APP_DRAWER) {
      viewModel.setSearchQuery("")
      viewModel.setScreen(LauncherScreen.HOME)
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    HomeScreen(
      currentTime = currentTime,
      currentDate = currentDate,
      weather = weather,
      toggles = toggles,
      fitness = fitness,
      audio = audio,
      quickNote = quickNote,
      storagePct = storagePct,
      ramPct = ramPct,
      folders = folders,
      pinnedApps = pinnedApps,
      dockApps = dockApps,
      settings = settings,
      onAppClick = { app -> viewModel.launchApp(app) },
      onOpenFolder = { folder -> viewModel.openFolder(folder) },
      onToggleFolderEnlarged = { folderId -> viewModel.toggleFolderEnlarged(folderId) },
      onToggleTorch = { viewModel.toggleTorch() },
      onCycleSound = { viewModel.cycleSoundMode() },
      onToggleWeather = { viewModel.toggleWeatherCondition() },
      onAddStep = { viewModel.addSteps() },
      onToggleAudioPlay = { viewModel.toggleAudioPlayback() },
      onNextAudioTrack = { viewModel.nextAudioTrack() },
      onEditNote = { isEditingNote = true },
      onOpenDrawer = { viewModel.setScreen(LauncherScreen.APP_DRAWER) },
      onOpenSettings = { isSettingsOpen = true },
      onOpenAppInfo = { app -> selectedAppForOptions = app },
      onSwipeUp = {
        if (currentScreen == LauncherScreen.HOME) {
          viewModel.setSearchQuery("")
          viewModel.setScreen(LauncherScreen.APP_DRAWER)
        }
      },
      onSwipeDown = { viewModel.openNotificationsPanel() },
      onDoubleTap = { viewModel.lockScreen() },
      onToggleThemeMode = {
        val newTheme = when (settings.themeMode) {
          LauncherThemeMode.DARK -> LauncherThemeMode.LIGHT
          LauncherThemeMode.LIGHT -> LauncherThemeMode.RETRO_PASTEL
          LauncherThemeMode.RETRO_PASTEL -> LauncherThemeMode.DARK
          LauncherThemeMode.SYSTEM -> LauncherThemeMode.DARK
        }
        viewModel.updateSettings(settings.copy(themeMode = newTheme))
      },
      onToggleClockStyle = {
        val newClock = if (settings.clockStyle == LauncherClockStyle.ANALOG) {
          LauncherClockStyle.DIGITAL
        } else {
          LauncherClockStyle.ANALOG
        }
        viewModel.updateSettings(settings.copy(clockStyle = newClock))
      },
      onReorderPinnedApps = { from, to -> viewModel.movePinnedApp(from, to) },
      onRemovePinnedApp = { app -> viewModel.removePinnedApp(app) },
      onToggleDockApp = { app -> viewModel.toggleDockApp(app) },
      onToggleWidget = { widgetType -> viewModel.toggleWidgetActive(widgetType) },
      isDrawerOpen = currentScreen == LauncherScreen.APP_DRAWER,
      onWidgetScaleChange = { widgetType, scale -> viewModel.setWidgetScale(widgetType, scale) },
      onResetWidgetScale = { widgetType -> viewModel.resetWidgetScale(widgetType) },
      onWidgetInfo = { widgetType -> viewModel.openWidgetAppInfo(widgetType) }
    )

    AnimatedVisibility(
      visible = currentScreen == LauncherScreen.APP_DRAWER,
      enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
      AppDrawerSheet(
        apps = installedApps,
        searchQuery = searchQuery,
        onSearchChange = { viewModel.setSearchQuery(it) },
        onAppClick = { app -> viewModel.launchApp(app) },
        onTogglePin = { app -> viewModel.togglePinApp(app) },
        onToggleDock = { app -> viewModel.toggleDockApp(app) },
        onOpenAppInfo = { app -> selectedAppForOptions = app },
        onClose = {
          viewModel.setSearchQuery("")
          viewModel.setScreen(LauncherScreen.HOME)
        },
        iconPack = settings.iconPack,
        accentColor = accentColor,
        onToggleThemeMode = {
          val newTheme = when (settings.themeMode) {
            LauncherThemeMode.DARK -> LauncherThemeMode.LIGHT
            LauncherThemeMode.LIGHT -> LauncherThemeMode.RETRO_PASTEL
            LauncherThemeMode.RETRO_PASTEL -> LauncherThemeMode.DARK
            LauncherThemeMode.SYSTEM -> LauncherThemeMode.DARK
          }
          viewModel.updateSettings(settings.copy(themeMode = newTheme))
        },
        onSelectIconPack = { pack -> viewModel.updateSettings(settings.copy(iconPack = pack)) },
        onOpenSettings = { isSettingsOpen = true }
      )
    }

    activeFolder?.let { folder ->
      ExpandedFolderSheet(
        folder = folder,
        onDismiss = { viewModel.openFolder(null) },
        onAppClick = { app -> viewModel.launchApp(app) },
        onToggleEnlarged = { viewModel.toggleFolderEnlarged(folder.id) },
        iconPack = settings.iconPack,
        accentColor = accentColor
      )
    }

    selectedAppForOptions?.let { app ->
      AppOptionsSheet(
        app = app,
        isPinned = pinnedApps.any { it.packageName == app.packageName },
        isDocked = dockApps.any { it.packageName == app.packageName },
        accentColor = accentColor,
        onOpenApp = { viewModel.launchApp(app) },
        onTogglePin = { viewModel.togglePinApp(app) },
        onToggleDock = { viewModel.toggleDockApp(app) },
        onOpenAppInfo = { viewModel.openAppInfo(app) },
        onDismiss = { selectedAppForOptions = null }
      )
    }

    if (isSettingsOpen) {
      LauncherSettingsDialog(
        settings = settings,
        onUpdateSettings = { viewModel.updateSettings(it) },
        onPickCustomWallpaper = { uri, target -> viewModel.setCustomWallpaper(uri, target) },
        onRemoveCustomWallpaper = { target -> viewModel.clearCustomWallpaper(target) },
        onLockScreenNow = { viewModel.lockLauncherScreen() },
        onDismiss = { isSettingsOpen = false },
        accentColor = accentColor
      )
    }

    if (isEditingNote) {
      EditNoteDialog(
        initialNote = quickNote,
        onSave = { viewModel.updateQuickNote(it) },
        onDismiss = { isEditingNote = false },
        accentColor = accentColor
      )
    }

    AnimatedVisibility(
      visible = currentScreen == LauncherScreen.LOCK_SCREEN,
      enter = fadeIn(androidx.compose.animation.core.tween(300)),
      exit = fadeOut(androidx.compose.animation.core.tween(250)) +
          slideOutVertically(targetOffsetY = { -it / 3 })
    ) {
      NothingLockScreen(
        currentTime = currentTime,
        currentDate = currentDate,
        weather = weather,
        fitness = fitness,
        toggles = toggles,
        notifications = notifications,
        settings = settings,
        onUnlock = { viewModel.unlockLauncherScreen() },
        onToggleTorch = { viewModel.toggleTorch() },
        onLaunchShortcut = { shortcut -> viewModel.launchShortcut(shortcut) },
        onDismissNotification = { id -> viewModel.dismissNotification(id) }
      )
    }
  }
}
