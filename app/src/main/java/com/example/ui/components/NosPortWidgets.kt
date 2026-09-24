package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NosWidgetPortType
import com.example.model.WeatherInfo
import com.example.service.SystemPortHelper
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic NOS 3.5 & KWGT Widgets Port Suite for Pixel 8 & Android 17.
 * Reproduces the widgets from user reference screenshots:
 * - JUL TUESDAY 07H 10M (Calendar + Digital Time with Dot Matrix)
 * - 4-Circle Mini Cluster (Temp, Dot-Matrix Cloud, ECG pulse wave, Red Recorder)
 * - Retro Text Glance ("TODAY IS TUESDAY AND TIME IS...")
 * - Decibel Sound Meter ("103 dB" with vertical LED dots)
 * - Circular Gauges (73% Music, 57°C Red Flame, 98% Bell)
 * - NOS Tasks Checklist ("Get groceries. Read a book...")
 * - Favorite Contact Pill
 */

/**
 * 1. Calendar & Digital Time Widget (Screenshot 2: JUL TUESDAY 07H 10M)
 */
@Composable
fun NosCalendarDigitalTimeWidget(
  currentTime: String,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onCalendarClick: () -> Unit = {},
  onClockClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val cal = remember { Calendar.getInstance() }
  val monthStr = remember { SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase() }
  val dayStr = remember { SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase() }

  val parts = currentTime.split(":")
  val hours = parts.getOrNull(0) ?: "07"
  val minutes = parts.getOrNull(1) ?: "10"

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable { onCalendarClick() }
      .padding(horizontal = 20.dp, vertical = 18.dp)
      .testTag("nos_calendar_digital_time_widget")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top row: Month in dot matrix + calendar icon + day name
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          DotMatrixString(
            text = monthStr,
            litColor = theme.textPrimary,
            unlitColor = theme.unlitDot,
            dotSize = 3.dp,
            dotSpacing = 1.dp,
            showUnlitDots = false
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = dayStr,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onCalendarClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Pixel Calendar",
            tint = theme.textPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom row: 07 H 10 M in authentic Nothing dot matrix
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onClockClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        // Hours
        DotMatrixString(
          text = hours,
          litColor = theme.textPrimary,
          unlitColor = theme.unlitDot,
          dotSize = 3.6.dp,
          dotSpacing = 1.2.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(6.dp))

        // "H"
        DotMatrixString(
          text = "H",
          litColor = accentColor,
          unlitColor = theme.unlitDot,
          dotSize = 2.4.dp,
          dotSpacing = 1.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Minutes
        DotMatrixString(
          text = minutes,
          litColor = theme.textPrimary,
          unlitColor = theme.unlitDot,
          dotSize = 3.6.dp,
          dotSpacing = 1.2.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(6.dp))

        // "M"
        DotMatrixString(
          text = "M",
          litColor = accentColor,
          unlitColor = theme.unlitDot,
          dotSize = 2.4.dp,
          dotSpacing = 1.dp,
          showUnlitDots = false
        )
      }
    }
  }
}

/**
 * 2. 4-Circle Mini Cluster Widget (Screenshot 2)
 * Arranged in a 2x2 grid:
 * - Circle 1: Temp (14°)
 * - Circle 2: Dot-matrix Cloud glyph
 * - Circle 3: White ECG pulse wave circle
 * - Circle 4: Red Camera / Recorder circle
 */
@Composable
fun NosMiniClusterWidget(
  weather: WeatherInfo,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onWeatherClick: () -> Unit = {},
  onHealthClick: () -> Unit = {},
  onRecorderClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current

  Column(
    modifier = modifier.testTag("nos_mini_cluster_widget"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Row 1: Temp + Cloud glyph
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Circle 1: Temp (e.g. 14°)
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onWeatherClick() },
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Text(
            text = "${weather.tempC}",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "°",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
      }

      // Circle 2: Dot-Matrix Cloud Glyph
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onWeatherClick() },
        contentAlignment = Alignment.Center
      ) {
        DotMatrixCloudGlyph(
          color = theme.textPrimary,
          modifier = Modifier.size(34.dp)
        )
      }
    }

    // Row 2: ECG Pulse Wave + Red Recorder
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Circle 3: White ECG Pulse Wave
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(if (theme.isDark) Color(0xFFE2E6E2) else Color.White)
          .border(1.dp, Color(0xFFCCCCCC), CircleShape)
          .clickable { onHealthClick() },
        contentAlignment = Alignment.Center
      ) {
        EcgPulseWaveCanvas(
          lineColor = Color(0xFF161616),
          modifier = Modifier
            .width(42.dp)
            .height(24.dp)
        )
      }

      // Circle 4: Nothing Red Recorder / Action Pill
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(accentColor)
          .clickable { onRecorderClick() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Videocam,
          contentDescription = "Quick Recorder / Camera",
          tint = NothingWhite,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

/**
 * 3. Text Glance Summary Widget (Screenshot 2)
 * Retro monospace status banner:
 * "TODAY IS TUESDAY AND TIME IS 7:06 AM. IT IS 14°C OUTSIDE AND CONDITION IS OVERCAST CLOUDS..."
 */
@Composable
fun NosGlanceTextWidget(
  currentTime: String,
  weather: WeatherInfo,
  batteryPct: Int,
  isCharging: Boolean,
  modifier: Modifier = Modifier,
  onGlanceClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val cal = remember { Calendar.getInstance() }
  val dayName = remember { SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase() }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(22.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(22.dp))
      .clickable { onGlanceClick() }
      .padding(18.dp)
      .testTag("nos_glance_text_widget")
  ) {
    Column {
      Text(
        text = "TODAY IS $dayName AND TIME IS $currentTime. IT IS ${weather.tempC}°C OUTSIDE AND CONDITION IS ${weather.condition} CLOUDS. BATTERY LEVEL IS $batteryPct% AND ${if (isCharging) "CHARGING" else "DISCHARGING"}.",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Wed 12:00-11:59 • Muharram & Pixel Glance",
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        color = theme.textSecondary,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * 4. Decibel Sound Meter Widget (Screenshot 1: 103 dB with vertical dot LED column)
 */
@Composable
fun NosDecibelWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  var dbValue by remember { mutableIntStateOf(103) }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable {
        dbValue = (75..110).random()
      }
      .padding(18.dp)
      .testTag("nos_decibel_widget")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "$dbValue",
          fontFamily = FontFamily.Monospace,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary
        )
        Text(
          text = "dB",
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          color = theme.textSecondary,
          modifier = Modifier.padding(bottom = 6.dp)
        )
      }

      // Vertical LED Dot Meter (Screenshot 1: dots stack with red peak)
      Column(
        verticalArrangement = Arrangement.spacedBy(3.5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val totalDots = 10
        val activeDots = ((dbValue - 60).coerceIn(0, 50) / 5)
        for (i in (totalDots - 1) downTo 0) {
          val isLit = i < activeDots
          val dotColor = when {
            !isLit -> theme.unlitDot
            i >= 8 -> NothingRed
            i >= 6 -> Color(0xFFFFB300)
            else -> theme.textPrimary
          }
          Box(
            modifier = Modifier
              .size(5.dp)
              .clip(CircleShape)
              .background(dotColor)
          )
        }
      }
    }
  }
}

/**
 * 5. NOS 3.5 Circular Progress Gauges (Screenshot 1)
 * - Music 73% circular arc
 * - Red Flame 57°C Device / CPU Temp
 * - Notification Bell 98% circular arc
 */
@Composable
fun NosCircularGaugesWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Music 73% circular ring
    NosProgressRingCircle(
      icon = Icons.Default.MusicNote,
      percent = 73,
      label = "73%",
      accentColor = accentColor,
      modifier = Modifier.weight(1f)
    )

    // 2. Red Flame 57°C (Pixel 8 Thermal & Performance)
    Box(
      modifier = Modifier
        .weight(1f)
        .height(100.dp)
        .clip(CircleShape)
        .background(NothingRed)
        .border(1.dp, NothingRed, CircleShape)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = "Device Temp",
          tint = NothingWhite,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "57°C",
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite
        )
      }
    }

    // 3. Bell 98% circular ring
    NosProgressRingCircle(
      icon = Icons.Default.Notifications,
      percent = 98,
      label = "98%",
      accentColor = accentColor,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun NosProgressRingCircle(
  icon: ImageVector,
  percent: Int,
  label: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .height(100.dp)
      .clip(CircleShape)
      .background(theme.surface)
      .border(1.dp, theme.border, CircleShape)
      .padding(6.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokeW = 4.dp.toPx()
      val radius = (size.minDimension - strokeW) / 2f
      val center = Offset(size.width / 2f, size.height / 2f)

      // Background track
      drawCircle(
        color = theme.elevated,
        radius = radius,
        center = center,
        style = Stroke(width = strokeW)
      )

      // Foreground arc
      val sweep = 360f * (percent / 100f)
      drawArc(
        color = theme.textPrimary,
        startAngle = -90f,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeW, cap = StrokeCap.Round)
      )

      // Indicator white dot on tip of arc
      val angleRad = Math.toRadians((-90f + sweep).toDouble())
      val dotX = (center.x + radius * cos(angleRad)).toFloat()
      val dotY = (center.y + radius * sin(angleRad)).toFloat()
      drawCircle(
        color = accentColor,
        radius = 3.5.dp.toPx(),
        center = Offset(dotX, dotY)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = theme.textPrimary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
    }
  }
}

/**
 * 6. NOS Tasks & Checklist Widget (Screenshot 1)
 */
@Composable
fun NosQuickListWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val tasks = remember {
    mutableStateListOf(
      "Get groceries." to true,
      "Read a book." to false,
      "Play a game." to false,
      "Enjoy your work." to false,
      "Meditate. Breathe. Live." to false
    )
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(18.dp)
      .testTag("nos_quick_list_widget")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      tasks.forEachIndexed { index, (task, isDone) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              tasks[index] = task to !isDone
            },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) accentColor else theme.textSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = task,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (isDone) theme.textSecondary else theme.textPrimary,
            style = androidx.compose.ui.text.TextStyle(
              textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
          )
        }
      }
    }
  }
}

/**
 * 7. Favorite Contact Pill Widget (Screenshot 1)
 */
@Composable
fun NosContactPillWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  contactName: String = "Rohit Pakala...",
  onCall: () -> Unit = {},
  onChat: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(14.dp)
      .testTag("nos_contact_pill_widget")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Avatar circle
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(theme.elevated),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "RP",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
        }

        Column {
          Text(
            text = contactName,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "FAVORITE CONTACT",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            color = theme.textSecondary
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onCall() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            tint = theme.textPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onChat() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "Message",
            tint = theme.textPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

/**
 * Custom Canvas drawing for Nothing OS Dot Matrix Cloud
 */
@Composable
private fun DotMatrixCloudGlyph(
  color: Color,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val cols = 8
    val rows = 5
    val dotR = 1.8.dp.toPx()
    val step = 3.8.dp.toPx()

    // Cloud silhouette mask: 1 = dot lit
    val cloudMask = arrayOf(
      intArrayOf(0, 0, 1, 1, 1, 0, 0, 0),
      intArrayOf(0, 1, 1, 1, 1, 1, 1, 0),
      intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
      intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
      intArrayOf(0, 1, 1, 1, 1, 1, 1, 0)
    )

    val startX = (size.width - (cols * step)) / 2f + dotR
    val startY = (size.height - (rows * step)) / 2f + dotR

    for (r in 0 until rows) {
      for (c in 0 until cols) {
        if (cloudMask[r][c] == 1) {
          drawCircle(
            color = color,
            radius = dotR,
            center = Offset(startX + c * step, startY + r * step)
          )
        }
      }
    }
  }
}

/**
 * Custom Canvas drawing for ECG pulse wave (Screenshot 2)
 */
@Composable
private fun EcgPulseWaveCanvas(
  lineColor: Color,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val path = Path().apply {
      val w = size.width
      val h = size.height
      val midY = h / 2f

      moveTo(0f, midY)
      lineTo(w * 0.25f, midY)
      lineTo(w * 0.35f, midY - h * 0.25f)
      lineTo(w * 0.45f, midY + h * 0.45f)
      lineTo(w * 0.60f, midY - h * 0.45f)
      lineTo(w * 0.70f, midY + h * 0.20f)
      lineTo(w * 0.80f, midY)
      lineTo(w, midY)
    }

    drawPath(
      path = path,
      color = lineColor,
      style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
  }
}

/**
 * 8. Bottom Sheet for Managing / Toggling NOS 3.5 Widgets Port on Pixel 8 / Android 17
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosWidgetPortSheet(
  activeWidgets: List<NosWidgetPortType>,
  onToggleWidget: (NosWidgetPortType) -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.background,
    scrimColor = Color.Black.copy(alpha = 0.65f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Widgets,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "NOS 3.5 WIDGETS PORT",
              fontFamily = FontFamily.Monospace,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "PIXEL 8 • ANDROID 17 LAUNCHER INTEGRATION",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = theme.textSecondary
          )
        }

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = if (accentColor == Color.White) Color.Black else Color.White
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("DONE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // External KWGT launcher port action if present
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(14.dp))
          .clickable {
            // Attempt to launch KWGT port app or settings
            val intent = context.packageManager.getLaunchIntentForPackage("org.kustom.widget")
            if (intent != null) {
              context.startActivity(intent)
            } else {
              // fallback to web or system widget manager
              SystemPortHelper.launchPixelWeather(context)
            }
          }
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Extension,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "KWGT NOTHING WIDGETS LINK",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
              )
              Text(
                text = "Launch Kustom KWGT or third-party Nothing Port APKs",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = theme.textSecondary
              )
            }
          }
          Text(
            text = "OPEN",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
      }

      Text(
        text = "ACTIVE WIDGETS ON HOME SCREEN",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )

      // Toggles for each widget
      WidgetToggleItem(
        title = "CALENDAR + DIGITAL TIME",
        subtitle = "JUL TUESDAY 07H 10M (Screenshot 2)",
        checked = activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CALENDAR_DIGITAL_TIME) }
      )

      WidgetToggleItem(
        title = "4-CIRCLE MINI CLUSTER (2x2)",
        subtitle = "14° Temp, Cloud Glyph, ECG Pulse, Red Cam",
        checked = activeWidgets.contains(NosWidgetPortType.MINI_CLUSTER_2X2),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.MINI_CLUSTER_2X2) }
      )

      WidgetToggleItem(
        title = "TEXT GLANCE SUMMARY",
        subtitle = "TODAY IS TUESDAY AND TIME IS... (Screenshot 2)",
        checked = activeWidgets.contains(NosWidgetPortType.GLANCE_TEXT_SUMMARY),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.GLANCE_TEXT_SUMMARY) }
      )

      WidgetToggleItem(
        title = "CIRCULAR GAUGES (3 RINGS)",
        subtitle = "73% Music, 57°C Red Flame, 98% Bell (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.CIRCULAR_GAUGES),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CIRCULAR_GAUGES) }
      )

      WidgetToggleItem(
        title = "DECIBEL SOUND METER",
        subtitle = "103 dB with vertical LED dots (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.DECIBEL_SOUND_METER) }
      )

      WidgetToggleItem(
        title = "NOS TASKS CHECKLIST",
        subtitle = "Checklist with interactive items (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.QUICK_CHECKLIST) }
      )

      WidgetToggleItem(
        title = "FAVORITE CONTACT PILL",
        subtitle = "Contact card with quick call & message",
        checked = activeWidgets.contains(NosWidgetPortType.CONTACT_PILL),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CONTACT_PILL) }
      )

      WidgetToggleItem(
        title = "NOTHING OS MAIN CLOCK",
        subtitle = "Dot Matrix or Minimalist Analog Clock",
        checked = activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CLOCK_MAIN) }
      )

      WidgetToggleItem(
        title = "WEATHER & QUICK TOGGLES",
        subtitle = "Dynamic Weather + Torch & Sound switches",
        checked = activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.WEATHER_MAIN) }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun WidgetToggleItem(
  title: String,
  subtitle: String,
  checked: Boolean,
  accentColor: Color,
  onCheckedChange: (Boolean) -> Unit
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(12.dp))
      .clickable { onCheckedChange(!checked) }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
      Text(
        text = subtitle,
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        color = theme.textSecondary
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = if (accentColor == Color.White) Color.Black else Color.White,
        checkedTrackColor = accentColor,
        uncheckedTrackColor = theme.elevated
      )
    )
  }
}
