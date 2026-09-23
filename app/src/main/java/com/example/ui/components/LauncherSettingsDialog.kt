package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
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
            text = "CUSTOMIZATION",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NothingWhite,
            letterSpacing = 2.sp
          )
          Text(
            text = "NOTHING OS 5 (EXPERIENCE)",
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

      // Section 3: App Labels
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

      Spacer(modifier = Modifier.height(16.dp))

      // Section 4: Home Grid Columns
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
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (settings.gridColumns == 4) accentColor else NothingElevated)
              .clickable { onUpdateSettings(settings.copy(gridColumns = 4)) }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = "4x4",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = NothingWhite
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (settings.gridColumns == 5) accentColor else NothingElevated)
              .clickable { onUpdateSettings(settings.copy(gridColumns = 5)) }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = "5x5",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = NothingWhite
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Button: Set as Default Launcher
      Button(
        onClick = { openDefaultLauncherSettings(context) },
        colors = ButtonDefaults.buttonColors(
          containerColor = accentColor,
          contentColor = NothingWhite
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("set_default_launcher_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Set Default",
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "SET AS DEFAULT LAUNCHER",
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

private fun openDefaultLauncherSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    try {
      val fallbackIntent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(fallbackIntent)
    } catch (_: Exception) {}
  }
}
