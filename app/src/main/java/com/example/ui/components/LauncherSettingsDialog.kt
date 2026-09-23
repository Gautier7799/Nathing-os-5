package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QueryStats
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
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

val ACCENT_COLORS = listOf(
  NothingRed,
  NothingWhite,
  NothingOrange,
  NothingYellow
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsDialog(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  // Permission statuses
  var isNotificationGranted by remember { mutableStateOf(SystemIntegrationHelper.isNotificationListenerGranted(context)) }
  var isOverlayGranted by remember { mutableStateOf(SystemIntegrationHelper.isOverlayGranted(context)) }
  var isAccessibilityGranted by remember { mutableStateOf(SystemIntegrationHelper.isAccessibilityGranted(context)) }
  var isUsageGranted by remember { mutableStateOf(SystemIntegrationHelper.isUsageAccessGranted(context)) }
  var isDndGranted by remember { mutableStateOf(SystemIntegrationHelper.isDndPolicyGranted(context)) }
  var isDefaultLauncher by remember { mutableStateOf(SystemIntegrationHelper.isDefaultLauncher(context)) }

  // Automatically refresh permission statuses when user returns to app from system settings
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
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .testTag("settings_dialog")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SETTINGS & SYSTEM",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NothingWhite,
            letterSpacing = 2.sp
          )
          Text(
            text = "NOTHING OS 5.0 (DEEP INTEGRATION)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = NothingGrey
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Settings",
            tint = NothingWhite
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 1: Accent Color
      Text(
        text = "GLYPH ACCENT COLOR",
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
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
              .size(40.dp)
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

      Spacer(modifier = Modifier.height(24.dp))

      // Section 2: Icon Pack Style
      Text(
        text = "ICON PACK STYLE",
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = NothingGrey,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconPackOption(
          label = "MONO",
          selected = settings.iconPack == IconPackStyle.MONOCHROME,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          onUpdateSettings(settings.copy(iconPack = IconPackStyle.MONOCHROME))
        }

        IconPackOption(
          label = "DARK",
          selected = settings.iconPack == IconPackStyle.MINIMAL_DARK,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          onUpdateSettings(settings.copy(iconPack = IconPackStyle.MINIMAL_DARK))
        }

        IconPackOption(
          label = "SYSTEM",
          selected = settings.iconPack == IconPackStyle.SYSTEM_DEFAULT,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          onUpdateSettings(settings.copy(iconPack = IconPackStyle.SYSTEM_DEFAULT))
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section 3: App Labels & Grid Density
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SHOW APP LABELS",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NothingWhite
          )
          Text(
            text = "Display names beneath icons",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = NothingGrey
          )
        }
        Switch(
          checked = settings.showLabels,
          onCheckedChange = { onUpdateSettings(settings.copy(showLabels = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = NothingWhite,
            checkedTrackColor = accentColor,
            uncheckedTrackColor = NothingElevated
          )
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

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
            text = "${settings.gridColumns} Columns",
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

      Spacer(modifier = Modifier.height(28.dp))

      // Section 4: System Gestures
      Text(
        text = "GESTURES & CONTROLS",
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = NothingGrey,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "DOUBLE TAP TO SLEEP",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NothingWhite
          )
          Text(
            text = "Double tap empty space to turn off screen",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = NothingGrey
          )
        }
        Switch(
          checked = settings.doubleTapToSleep,
          onCheckedChange = {
            if (it && !isAccessibilityGranted) {
              SystemIntegrationHelper.openAccessibilitySettings(context)
            }
            onUpdateSettings(settings.copy(doubleTapToSleep = it))
          },
          colors = SwitchDefaults.colors(
            checkedThumbColor = NothingWhite,
            checkedTrackColor = accentColor,
            uncheckedTrackColor = NothingElevated
          )
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "SWIPE DOWN FOR NOTIFICATIONS",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NothingWhite
          )
          Text(
            text = "Pull down anywhere on home to expand shade",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = NothingGrey
          )
        }
        Switch(
          checked = settings.swipeDownNotifications,
          onCheckedChange = { onUpdateSettings(settings.copy(swipeDownNotifications = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = NothingWhite,
            checkedTrackColor = accentColor,
            uncheckedTrackColor = NothingElevated
          )
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Section 5: SYSTEM INTEGRATION & PERMISSIONS
      Text(
        text = "SYSTEM INTEGRATION & PERMISSIONS",
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(12.dp))

      // 1. Notification Access (Dots & Badges)
      PermissionIntegrationCard(
        icon = Icons.Default.Notifications,
        title = "NOTIFICATION LISTENER",
        description = "Displays live Nothing OS dot badges on app icons with unread messages",
        isGranted = isNotificationGranted,
        actionLabel = if (isNotificationGranted) "ACTIVE" else "GRANT",
        accentColor = accentColor,
        onClick = { SystemIntegrationHelper.openNotificationListenerSettings(context) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Superposition / Overlay (Display over other apps)
      PermissionIntegrationCard(
        icon = Icons.Default.Layers,
        title = "SUPERPOSITION (OVERLAY)",
        description = "Allows floating widgets, glyph light overlays & gesture handles over other apps",
        isGranted = isOverlayGranted,
        actionLabel = if (isOverlayGranted) "ACTIVE" else "GRANT",
        accentColor = accentColor,
        onClick = { SystemIntegrationHelper.requestOverlayPermission(context) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Accessibility Service (Gestures & Sleep)
      PermissionIntegrationCard(
        icon = Icons.Default.AccessibilityNew,
        title = "ACCESSIBILITY SERVICE",
        description = "Provides native OS control for double-tap screen lock and notification panel expansion",
        isGranted = isAccessibilityGranted,
        actionLabel = if (isAccessibilityGranted) "ACTIVE" else "ENABLE",
        accentColor = accentColor,
        onClick = { SystemIntegrationHelper.openAccessibilitySettings(context) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Usage Access (Digital Wellbeing)
      PermissionIntegrationCard(
        icon = Icons.Default.QueryStats,
        title = "USAGE STATS ACCESS",
        description = "Allows Nothing OS to calculate screen time, frequent apps and resource metrics",
        isGranted = isUsageGranted,
        actionLabel = if (isUsageGranted) "ACTIVE" else "GRANT",
        accentColor = accentColor,
        onClick = { SystemIntegrationHelper.openUsageAccessSettings(context) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 5. Do Not Disturb Policy
      PermissionIntegrationCard(
        icon = Icons.Default.DoNotDisturb,
        title = "DO NOT DISTURB ACCESS",
        description = "Allows toggling sound profile between Silent, Vibrate, and Normal from widgets",
        isGranted = isDndGranted,
        actionLabel = if (isDndGranted) "ACTIVE" else "GRANT",
        accentColor = accentColor,
        onClick = { SystemIntegrationHelper.openDndPolicySettings(context) }
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Primary Default Launcher Button
      Button(
        onClick = { SystemIntegrationHelper.openDefaultLauncherSettings(context) },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDefaultLauncher) NothingDarkSurface else accentColor,
          contentColor = NothingWhite
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .border(
            width = if (isDefaultLauncher) 1.dp else 0.dp,
            color = if (isDefaultLauncher) Color(0xFF00E676) else Color.Transparent,
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("set_default_launcher_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = if (isDefaultLauncher) Icons.Default.Check else Icons.Default.Home,
            contentDescription = "Set Default",
            tint = if (isDefaultLauncher) Color(0xFF00E676) else NothingWhite,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = if (isDefaultLauncher) "DEFAULT LAUNCHER (ACTIVE)" else "SET AS DEFAULT LAUNCHER",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
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
      .clip(RoundedCornerShape(14.dp))
      .background(NothingDarkSurface)
      .border(
        width = 1.dp,
        color = if (isGranted) Color(0xFF00E676).copy(alpha = 0.4f) else NothingBorder,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable { onClick() }
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (isGranted) Color(0xFF00E676).copy(alpha = 0.15f) else NothingElevated),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = if (isGranted) Color(0xFF00E676) else NothingGrey,
          modifier = Modifier.size(18.dp)
        )
      }

      Column {
        Text(
          text = title,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = description,
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          color = NothingGrey,
          lineHeight = 13.sp
        )
      }
    }

    Spacer(modifier = Modifier.size(10.dp))

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(
          if (isGranted) Color(0xFF00E676).copy(alpha = 0.18f) else accentColor
        )
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(
        text = actionLabel,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isGranted) Color(0xFF00E676) else NothingWhite
      )
    }
  }
}

@Composable
private fun IconPackOption(
  label: String,
  selected: Boolean,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (selected) accentColor else NothingDarkSurface)
      .border(1.dp, if (selected) accentColor else NothingBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontFamily = FontFamily.Monospace,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = NothingWhite
    )
  }
}
