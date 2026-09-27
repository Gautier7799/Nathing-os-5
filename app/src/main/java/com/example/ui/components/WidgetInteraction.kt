package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NosWidgetPortType
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed
import kotlin.math.abs
import kotlin.math.hypot

private const val MIN_WIDGET_SCALE = 0.75f
private const val MAX_WIDGET_SCALE = 1.5f

@Composable
fun ScalableWidget(
  widgetType: NosWidgetPortType,
  scale: Float,
  onScaleChange: (Float) -> Unit,
  onReset: () -> Unit,
  onWidgetInfo: () -> Unit = {},
  accentColor: Color = NothingRed,
  content: @Composable () -> Unit
) {
  var showActions by remember(widgetType) { mutableStateOf(false) }
  val haptic = LocalHapticFeedback.current
  val clamped = scale.coerceIn(MIN_WIDGET_SCALE, MAX_WIDGET_SCALE)
  var gestureScale by androidx.compose.runtime.remember(widgetType) { mutableFloatStateOf(clamped) }

  LaunchedEffect(clamped) {
    gestureScale = clamped
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .pointerInput(widgetType) {
        // Stability rule: one finger belongs to the Home LazyColumn.
        // Only a real two-finger gesture is allowed to resize this widget.
        awaitEachGesture {
          awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
          var resizing = false
          var changed = false
          var lastHapticScale = gestureScale

          while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val pressedCount = event.changes.count { it.pressed }

            if (pressedCount >= 2) {
              resizing = true
              val pointers = event.changes.filter { it.pressed }.take(2)
              val zoom = if (pointers.size == 2) {
                val currentDistance = hypot(
                  pointers[0].position.x - pointers[1].position.x,
                  pointers[0].position.y - pointers[1].position.y
                )
                val previousDistance = hypot(
                  pointers[0].previousPosition.x - pointers[1].previousPosition.x,
                  pointers[0].previousPosition.y - pointers[1].previousPosition.y
                )
                if (previousDistance > 0.5f) currentDistance / previousDistance else 1f
              } else {
                1f
              }
              if (abs(zoom - 1f) > 0.008f) {
                val next = (gestureScale * zoom).coerceIn(MIN_WIDGET_SCALE, MAX_WIDGET_SCALE)
                if (abs(next - gestureScale) > 0.001f) {
                  gestureScale = next
                  changed = true
                  if (abs(gestureScale - lastHapticScale) >= 0.05f) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastHapticScale = gestureScale
                  }
                }
              }
              event.changes.forEach { it.consume() }
            } else if (resizing) {
              if (changed) {
                // Persist once at the end, not on every gesture frame.
                onScaleChange(gestureScale.coerceIn(MIN_WIDGET_SCALE, MAX_WIDGET_SCALE))
              }
              break
            }

            if (event.changes.all { !it.pressed }) {
              if (resizing && changed) {
                onScaleChange(gestureScale.coerceIn(MIN_WIDGET_SCALE, MAX_WIDGET_SCALE))
              }
              break
            }
          }
        }
      }
      .graphicsLayer {
        scaleX = gestureScale
        scaleY = gestureScale
        transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
      },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    content()

    Spacer(Modifier.height(3.dp))
    Row(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.10f))
        .clickable {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          showActions = true
        },
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Tune,
        contentDescription = "Widget options",
        tint = accentColor,
        modifier = Modifier.size(17.dp)
      )
    }
  }

  if (showActions) {
    WidgetActionSheet(
      widgetType = widgetType,
      scale = gestureScale,
      accentColor = accentColor,
      onDismiss = { showActions = false },
      onZoomIn = {
        val next = (gestureScale + 0.10f).coerceAtMost(MAX_WIDGET_SCALE)
        gestureScale = next
        onScaleChange(next)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      },
      onZoomOut = {
        val next = (gestureScale - 0.10f).coerceAtLeast(MIN_WIDGET_SCALE)
        gestureScale = next
        onScaleChange(next)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      },
      onReset = {
        onReset()
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        showActions = false
      },
      onWidgetInfo = {
        onWidgetInfo()
        showActions = false
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetActionSheet(
  widgetType: NosWidgetPortType,
  scale: Float,
  accentColor: Color,
  onDismiss: () -> Unit,
  onZoomIn: () -> Unit,
  onZoomOut: () -> Unit,
  onReset: () -> Unit,
  onWidgetInfo: () -> Unit
) {
  val theme = LocalLauncherTheme.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val title = widgetType.name.replace('_', ' ')

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.surface,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.layout.Box(
          modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
        Spacer(Modifier.size(10.dp))
        Text(
          text = title,
          color = theme.textPrimary,
          fontSize = 17.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
      }
      Spacer(Modifier.height(8.dp))
      Text(
        text = "SIZE ${"%.0f".format(scale * 100)}%  •  PINCH TO RESIZE",
        color = theme.textSecondary,
        fontSize = 10.sp,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
      )
      Spacer(Modifier.height(14.dp))

      WidgetActionRow("ZOOM IN", Icons.Default.ZoomIn, accentColor, onZoomIn, theme.textPrimary)
      WidgetActionRow("ZOOM OUT", Icons.Default.ZoomOut, accentColor, onZoomOut, theme.textPrimary)
      WidgetActionRow("RESET WIDGET SIZE", Icons.Default.Refresh, accentColor, onReset, theme.textPrimary)
      WidgetActionRow("APP INFO", Icons.Default.Info, accentColor, onWidgetInfo, theme.textPrimary)
      Spacer(Modifier.height(22.dp))
    }
  }
}

@Composable
private fun WidgetActionRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  textColor: Color
) {
  val haptic = LocalHapticFeedback.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
      }
      .padding(horizontal = 8.dp, vertical = 13.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.10f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(19.dp))
    }
    Text(title, color = textColor, fontSize = 13.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
  }
}