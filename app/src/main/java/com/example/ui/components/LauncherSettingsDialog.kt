package com.example.ui.components

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
import com.example.model.LockClockStyle
import com.example.model.LockSecurityType
import com.example.model.LockShortcutType
import com.example.model.WallpaperTarget
import com.example.service.SystemIntegrationHelper
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingMatteBlack
import com.example.ui.theme.NothingOrange
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import com.example.ui.theme.NothingYellow
import java.io.File

val ACCENT_COLORS = listOf(
  NothingRed,
  NothingWhite,
  NothingOrange,
  NothingYellow
)

data class WallpaperChoice(
  val index: Int,
  val name: String,
  val badge: String,
  val desc: String
)

val WALLPAPER_CHOICES = listOf(
  WallpaperChoice(0, "DOT MATRIX NOIR", "SIGNATURE", "Nothing signature white dots grid"),
  WallpaperChoice(1, "CARBON MATTE", "STEALTH", "Pure deep black Nothing finish"),
  WallpaperChoice(2, "RED CIRCUIT GLOW", "CYBER", "Red electronic traces & glowing nodes"),
  WallpaperChoice(3, "LIGHT MONOCHROME", "MINIMAL", "Monochrome high-contrast inverted dots"),
  WallpaperChoice(4, "NOTHING GLYPH", "ARTWORK", "Phone (2) signature Glyph light geometry"),
  WallpaperChoice(5, "RED ECLIPSE", "GRADIENT", "Crimson abstract minimalist gradient"),
  WallpaperChoice(6, "RETRO WIREFRAME", "VECTOR", "Futuristic 3D isometric perspective grid"),
  WallpaperChoice(7, "CUSTOM GALLERY PHOTO", "GALLERY", "User photo loaded from phone storage")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsDialog(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onPickCustomWallpaper: (android.net.Uri, WallpaperTarget) -> Unit = { _, _ -> },
  onLockScreenNow: () -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  // Selected tab: 0 = HOME, 1 = LOCK SCREEN, 2 = THEMES, 3 = PERMISSIONS
  var selectedTab by remember { mutableIntStateOf(1) } // Start on Lock Screen tab as requested
  var wallpaperTarget by remember { mutableStateOf(WallpaperTarget.BOTH) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onPickCustomWallpaper(uri, wallpaperTarget)
    }
  }

  // Permission statuses
  var isNotificationGranted by remember { mutableStateOf(SystemIntegrationHelper.isNotificationListenerGranted(context)) }
  var isOverlayGranted by remember { mutableStateOf(SystemIntegrationHelper.isOverlayGranted(context)) }
  var isAccessibilityGranted by remember { mutableStateOf(SystemIntegrationHelper.isAccessibilityGranted(context)) }
  var isUsageGranted by remember { mutableStateOf(SystemIntegrationHelper.isUsageAccessGranted(context)) }
  var isDndGranted by remember { mutableStateOf(SystemIntegrationHelper.isDndPolicyGranted(context)) }
  var isDefaultLauncher by remember { mutableStateOf(SystemIntegrationHelper.isDefaultLauncher(context)) }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isNotificationGranted = SystemIntegrationHelper.isNotificationListenerGranted(context)
        isOverlayGranted = SystemIntegrationHelper.isOverlayGranted(context)
        isAccessibilityGranted = SystemIntegrationHelper.isAccessibilityGranted(context)
        isUsageGranted = SystemIntegrationHelper.isUsageAccessGranted(context)
        isDndGranted = SystemIntegrationHelper.isDndPolicyGranted(context)
        isDefaultLauncher = SystemIntegrationHelper.isDefaultLauncher(context)
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = NothingMatteBlack,
    contentColor = NothingWhite
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .testTag("launcher_settings_dialog")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "NOTHING OS 5.0",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NothingWhite,
            letterSpacing = 2.sp
          )
          Text(
            text = "LAUNCHER & LOCK SCREEN PREFERENCES",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = NothingGrey
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = NothingWhite
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tab selector row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingDarkSurface)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        TabButton(
          title = "LOCK",
          icon = Icons.Default.Lock,
          selected = selectedTab == 1,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 1 }

        TabButton(
          title = "HOME",
          icon = Icons.Default.Home,
          selected = selectedTab == 0,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 0 }

        TabButton(
          title = "THEME",
          icon = Icons.Default.Palette,
          selected = selectedTab == 2,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 2 }

        TabButton(
          title = "SYSTEM",
          icon = Icons.Default.Security,
          selected = selectedTab == 3,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 3 }
      }

      Spacer(modifier = Modifier.height(20.dp))

      when (selectedTab) {
        // TAB 1: LOCK SCREEN (NOTHING OS 5)
        1 -> {
          val lock = settings.lockScreen

          // Preview / Lock Now Banner
          Button(
            onClick = {
              onDismiss()
              onLockScreenNow()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = accentColor,
              contentColor = NothingWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("preview_lock_screen_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                text = "TEST / LOCK SCREEN NOW",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Enable Nothing Lockscreen
          SettingsSwitchRow(
            title = "NOTHING OS 5 LOCK SCREEN",
            subtitle = "Enable signature Nothing lock experience",
            checked = lock.isLockScreenEnabled,
            accentColor = accentColor,
            onCheckedChange = {
              onUpdateSettings(settings.copy(lockScreen = lock.copy(isLockScreenEnabled = it)))
            }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Lock Clock Style
          Text(
            text = "LOCK CLOCK STYLE",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              LockClockStyle.DOT_MATRIX_BIG to "BIG NDOT",
              LockClockStyle.VERTICAL_STACK to "STACK",
              LockClockStyle.MINIMAL_ANALOG to "ANALOG",
              LockClockStyle.CLASSIC_DIGITAL to "CLASSIC"
            ).forEach { (style, label) ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (lock.clockStyle == style) accentColor else NothingDarkSurface)
                  .border(1.dp, if (lock.clockStyle == style) accentColor else NothingBorder, RoundedCornerShape(10.dp))
                  .clickable {
                    onUpdateSettings(settings.copy(lockScreen = lock.copy(clockStyle = style)))
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Security Type (Swipe vs PIN)
          Text(
            text = "UNLOCK SECURITY",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              LockSecurityType.SWIPE to "SWIPE UP",
              LockSecurityType.PIN to "4-DIGIT PIN"
            ).forEach { (sec, label) ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (lock.securityType == sec) accentColor else NothingDarkSurface)
                  .border(1.dp, if (lock.securityType == sec) accentColor else NothingBorder, RoundedCornerShape(10.dp))
                  .clickable {
                    onUpdateSettings(settings.copy(lockScreen = lock.copy(securityType = sec)))
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
              }
            }
          }

          // If PIN is selected, PIN customizer
          if (lock.securityType == LockSecurityType.PIN) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NothingDarkSurface)
                .border(1.dp, NothingBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "CURRENT PIN CODE",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
                Text(
                  text = "Enter 4 numeric digits",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = NothingGrey
                )
              }
              BasicTextField(
                value = lock.pinCode,
                onValueChange = {
                  if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    onUpdateSettings(settings.copy(lockScreen = lock.copy(pinCode = it)))
                  }
                },
                textStyle = TextStyle(
                  color = accentColor,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(accentColor),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(NothingElevated)
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Lock Screen Features Toggles
          SettingsSwitchRow(
            title = "LOCK SCREEN WIDGETS",
            subtitle = "Weather & steps capsule pills",
            checked = lock.showWidgets,
            accentColor = accentColor,
            onCheckedChange = {
              onUpdateSettings(settings.copy(lockScreen = lock.copy(showWidgets = it)))
            }
          )

          Spacer(modifier = Modifier.height(12.dp))

          SettingsSwitchRow(
            title = "LOCK NOTIFICATIONS",
            subtitle = "Show unread notifications on lock screen",
            checked = lock.showNotifications,
            accentColor = accentColor,
            onCheckedChange = {
              onUpdateSettings(settings.copy(lockScreen = lock.copy(showNotifications = it)))
            }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Shortcuts config
          Text(
            text = "BOTTOM SHORTCUTS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("LEFT: TORCH", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NothingGrey)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("RIGHT: CAMERA", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NothingGrey)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Owner Info Field
          Text(
            text = "DEVICE OWNER INFO",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          BasicTextField(
            value = lock.customOwnerInfo,
            onValueChange = {
              onUpdateSettings(settings.copy(lockScreen = lock.copy(customOwnerInfo = it)))
            },
            textStyle = TextStyle(
              color = NothingWhite,
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp
            ),
            cursorBrush = SolidColor(accentColor),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(NothingDarkSurface)
              .border(1.dp, NothingBorder, RoundedCornerShape(10.dp))
              .padding(12.dp)
          )
        }

        // TAB 0: HOME SCREEN & ICONS
        0 -> {
          // Icon Pack Style
          Text(
            text = "ICON PACK STYLE",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              IconPackStyle.MONOCHROME to "MONO",
              IconPackStyle.MINIMAL_DARK to "DARK",
              IconPackStyle.SYSTEM_DEFAULT to "SYSTEM"
            ).forEach { (pack, label) ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (settings.iconPack == pack) accentColor else NothingDarkSurface)
                  .border(1.dp, if (settings.iconPack == pack) accentColor else NothingBorder, RoundedCornerShape(10.dp))
                  .clickable { onUpdateSettings(settings.copy(iconPack = pack)) }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Grid Columns (4x4 vs 5x5)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GRID DENSITY",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = NothingWhite
              )
              Text(
                text = "${settings.gridColumns} Columns Layout",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NothingGrey
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(4, 5).forEach { cols ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.gridColumns == cols) accentColor else NothingElevated)
                    .clickable { onUpdateSettings(settings.copy(gridColumns = cols)) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = "${cols}x${cols}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NothingWhite
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          SettingsSwitchRow(
            title = "SHOW APP LABELS",
            subtitle = "Display app titles underneath icons",
            checked = settings.showLabels,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(showLabels = it)) }
          )

          Spacer(modifier = Modifier.height(14.dp))

          SettingsSwitchRow(
            title = "DOCK SEARCH BAR",
            subtitle = "Show Google / App search on bottom dock",
            checked = settings.showSearchBarOnDock,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(showSearchBarOnDock = it)) }
          )
        }

        // TAB 2: THEMES & WALLPAPERS
        2 -> {
          // Accent Color
          Text(
            text = "NOTHING ACCENT COLOR",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            ACCENT_COLORS.forEachIndexed { index, color ->
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(color)
                  .border(
                    width = if (settings.accentColorIndex == index) 2.dp else 0.dp,
                    color = if (settings.accentColorIndex == index) NothingWhite else Color.Transparent,
                    shape = CircleShape
                  )
                  .clickable { onUpdateSettings(settings.copy(accentColorIndex = index)) },
                contentAlignment = Alignment.Center
              ) {
                if (settings.accentColorIndex == index) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color == NothingWhite) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(22.dp))

          // Wallpaper Destination Selector
          Text(
            text = "APPLY WALLPAPER TO",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              WallpaperTarget.HOME to "HOME ONLY",
              WallpaperTarget.LOCK to "LOCK ONLY",
              WallpaperTarget.BOTH to "BOTH SCREENS"
            ).forEach { (target, label) ->
              val isSelected = wallpaperTarget == target
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) accentColor else NothingDarkSurface)
                  .border(1.dp, if (isSelected) accentColor else NothingBorder, RoundedCornerShape(10.dp))
                  .clickable { wallpaperTarget = target }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else NothingGrey
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Custom Gallery Photo Launcher Button
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(NothingDarkSurface)
              .border(1.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AddPhotoAlternate,
                  contentDescription = "Pick photo",
                  tint = accentColor,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "CHOOSE PHOTO FROM GALLERY",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
                Text(
                  text = "Select any picture from device storage",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = NothingGrey
                )
              }
            }
            Icon(
              imageVector = Icons.Default.Image,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
          }

          // Active Custom Photo status indicator
          val activeCustomUri = when (wallpaperTarget) {
            WallpaperTarget.HOME -> settings.customWallpaperUri
            WallpaperTarget.LOCK -> settings.customLockScreenWallpaperUri ?: settings.customWallpaperUri
            WallpaperTarget.BOTH -> settings.customWallpaperUri ?: settings.customLockScreenWallpaperUri
          }
          if (!activeCustomUri.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NothingElevated)
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              val photoFile = File(activeCustomUri)
              AsyncImage(
                model = if (photoFile.exists()) photoFile else activeCustomUri,
                contentDescription = "Wallpaper preview",
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ACTIVE CUSTOM PHOTO",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = accentColor
                )
                Text(
                  text = "Wallpaper photo active on device",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = NothingGrey
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Dimming Scrim
          Text(
            text = "PHOTO DIMMING / CONTRAST",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Darkens custom photos so icons and clock remain sharp",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = NothingGrey
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              0 to "OFF (0%)",
              25 to "LIGHT (25%)",
              40 to "MEDIUM (40%)",
              60 to "DEEP (60%)"
            ).forEach { (pct, label) ->
              val isSelected = settings.wallpaperDimPct == pct
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) accentColor else NothingDarkSurface)
                  .border(1.dp, if (isSelected) accentColor else NothingBorder, RoundedCornerShape(8.dp))
                  .clickable { onUpdateSettings(settings.copy(wallpaperDimPct = pct)) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else NothingGrey
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Nothing OS 5 Wallpapers List
          Text(
            text = "NOTHING OS 5 WALLPAPERS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WALLPAPER_CHOICES.forEach { choice ->
              val isCurrent = when (wallpaperTarget) {
                WallpaperTarget.HOME -> settings.wallpaperIndex == choice.index
                WallpaperTarget.LOCK -> if (settings.lockScreenWallpaperIndex >= 0) settings.lockScreenWallpaperIndex == choice.index else settings.wallpaperIndex == choice.index
                WallpaperTarget.BOTH -> settings.wallpaperIndex == choice.index && (settings.lockScreenWallpaperIndex < 0 || settings.lockScreenWallpaperIndex == choice.index)
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isCurrent) NothingElevated else NothingDarkSurface)
                  .border(
                    width = 1.dp,
                    color = if (isCurrent) accentColor else NothingBorder,
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    if (choice.index == 7 && (settings.customWallpaperUri == null && settings.customLockScreenWallpaperUri == null)) {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    } else {
                      when (wallpaperTarget) {
                        WallpaperTarget.HOME -> onUpdateSettings(settings.copy(wallpaperIndex = choice.index))
                        WallpaperTarget.LOCK -> onUpdateSettings(settings.copy(lockScreenWallpaperIndex = choice.index))
                        WallpaperTarget.BOTH -> onUpdateSettings(settings.copy(wallpaperIndex = choice.index, lockScreenWallpaperIndex = choice.index))
                      }
                    }
                  }
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = choice.name,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = NothingWhite
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = choice.badge,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = choice.desc,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NothingGrey
                  )
                }

                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          SettingsSwitchRow(
            title = "HAPTIC FEEDBACK",
            subtitle = "Subtle vibration clicks on touch",
            checked = settings.hapticFeedbackEnabled,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedbackEnabled = it)) }
          )
        }

        // TAB 3: SYSTEM PERMISSIONS & GESTURES
        3 -> {
          // Gestures
          Text(
            text = "SYSTEM GESTURES",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          SettingsSwitchRow(
            title = "DOUBLE TAP TO SLEEP",
            subtitle = "Double tap home screen to lock",
            checked = settings.doubleTapToSleep,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(doubleTapToSleep = it)) }
          )

          Spacer(modifier = Modifier.height(10.dp))

          SettingsSwitchRow(
            title = "SWIPE DOWN FOR NOTIFICATIONS",
            subtitle = "Pull down anywhere on home for shade",
            checked = settings.swipeDownNotifications,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(swipeDownNotifications = it)) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          Text(
            text = "DEEP SYSTEM PERMISSIONS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.Notifications,
            title = "NOTIFICATION LISTENER",
            description = "Enables real-time app badges and lockscreen notifications",
            isGranted = isNotificationGranted,
            actionLabel = if (isNotificationGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openNotificationListenerSettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.Layers,
            title = "SUPERPOSITION (OVERLAY)",
            description = "Enables floating widgets & gesture overlays",
            isGranted = isOverlayGranted,
            actionLabel = if (isOverlayGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.requestOverlayPermission(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.AccessibilityNew,
            title = "ACCESSIBILITY SERVICE",
            description = "Enables native gestures (lock screen & shade pull)",
            isGranted = isAccessibilityGranted,
            actionLabel = if (isAccessibilityGranted) "ACTIVE" else "ENABLE",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openAccessibilitySettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.QueryStats,
            title = "USAGE STATS ACCESS",
            description = "Calculates screen time & frequent apps",
            isGranted = isUsageGranted,
            actionLabel = if (isUsageGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openUsageAccessSettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.DoNotDisturb,
            title = "DND / SOUND ACCESS",
            description = "Toggles Silent & Vibrate mode from widget",
            isGranted = isDndGranted,
            actionLabel = if (isDndGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openDndPolicySettings(context) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = { SystemIntegrationHelper.openDefaultLauncherSettings(context) },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDefaultLauncher) NothingDarkSurface else accentColor,
              contentColor = NothingWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .border(
                width = if (isDefaultLauncher) 1.dp else 0.dp,
                color = if (isDefaultLauncher) Color(0xFF00E676) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
              )
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = if (isDefaultLauncher) Icons.Default.Check else Icons.Default.Home,
                contentDescription = null,
                tint = if (isDefaultLauncher) Color(0xFF00E676) else NothingWhite,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (isDefaultLauncher) "DEFAULT LAUNCHER (ACTIVE)" else "SET AS DEFAULT LAUNCHER",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun TabButton(
  title: String,
  icon: ImageVector,
  selected: Boolean,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (selected) accentColor else Color.Transparent)
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (selected) (if (accentColor == NothingWhite) Color.Black else NothingWhite) else NothingGrey,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) (if (accentColor == NothingWhite) Color.Black else NothingWhite) else NothingGrey
      )
    }
  }
}

@Composable
private fun SettingsSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  accentColor: Color,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = NothingWhite
      )
      Text(
        text = subtitle,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = NothingGrey
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = NothingWhite,
        checkedTrackColor = accentColor,
        uncheckedTrackColor = NothingElevated
      )
    )
  }
}

@Composable
private fun PermissionIntegrationCard(
  icon: ImageVector,
  title: String,
  description: String,
  isGranted: Boolean,
  actionLabel: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(NothingDarkSurface)
      .border(
        width = 1.dp,
        color = if (isGranted) Color(0xFF00E676).copy(alpha = 0.35f) else NothingBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable { onClick() }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(if (isGranted) Color(0xFF00E676).copy(alpha = 0.15f) else NothingElevated),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = if (isGranted) Color(0xFF00E676) else NothingGrey,
          modifier = Modifier.size(16.dp)
        )
      }

      Column {
        Text(
          text = title,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite
        )
        Text(
          text = description,
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = NothingGrey,
          lineHeight = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.size(8.dp))

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(
          if (isGranted) Color(0xFF00E676).copy(alpha = 0.18f) else accentColor
        )
        .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
      Text(
        text = actionLabel,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (isGranted) Color(0xFF00E676) else NothingWhite
      )
    }
  }
}
