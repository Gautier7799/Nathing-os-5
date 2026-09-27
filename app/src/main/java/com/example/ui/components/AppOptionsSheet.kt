package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.ui.theme.LocalLauncherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOptionsSheet(
  app: AppItem,
  isPinned: Boolean,
  isDocked: Boolean,
  accentColor: Color,
  onOpenApp: () -> Unit,
  onTogglePin: () -> Unit,
  onToggleDock: () -> Unit,
  onOpenAppInfo: () -> Unit,
  onDismiss: () -> Unit
) {
  val theme = LocalLauncherTheme.current
  val haptic = LocalHapticFeedback.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.surface,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          androidx.compose.foundation.layout.Box(
            modifier = Modifier
              .size(9.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = app.label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = theme.textPrimary
          )
        }
      }

      AppOptionRow(
        title = "OPEN APP",
        icon = Icons.Default.OpenInNew,
        accentColor = accentColor,
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onDismiss()
          onOpenApp()
        }
      )

      AppOptionRow(
        title = if (isPinned) "REMOVE FROM HOME SCREEN" else "PIN TO HOME SCREEN",
        icon = if (isPinned) Icons.Default.Delete else Icons.Default.PushPin,
        accentColor = accentColor,
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onTogglePin()
          onDismiss()
        }
      )

      AppOptionRow(
        title = if (isDocked) "REMOVE FROM DOCK FAVORITES" else "ADD TO DOCK FAVORITES",
        icon = if (isDocked) Icons.Default.Delete else Icons.Default.Star,
        accentColor = accentColor,
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onToggleDock()
          onDismiss()
        }
      )

      AppOptionRow(
        title = "APP INFO & PERMISSIONS",
        icon = Icons.Default.Info,
        accentColor = accentColor,
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onDismiss()
          onOpenAppInfo()
        }
      )

      Spacer(Modifier.size(12.dp))
    }
  }
}

@Composable
private fun AppOptionRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit
) {
  val theme = LocalLauncherTheme.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = accentColor,
      modifier = Modifier.size(22.dp)
    )
    Text(
      text = title,
      color = theme.textPrimary,
      fontFamily = FontFamily.Monospace,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
