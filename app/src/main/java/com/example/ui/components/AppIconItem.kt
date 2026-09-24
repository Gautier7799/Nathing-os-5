package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
  app: AppItem,
  onClick: () -> Unit,
  onLongClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  iconSize: Dp = 54.dp,
  showLabel: Boolean = true,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val isDark = theme.isDark

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
      .padding(4.dp)
      .testTag("app_item_${app.packageName}"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(iconSize)
        .clip(CircleShape)
        .background(
          if (!isDark && iconPack != IconPackStyle.SYSTEM_DEFAULT) {
            Color.White
          } else {
            when (iconPack) {
              IconPackStyle.MONOCHROME -> NothingDarkSurface
              IconPackStyle.MINIMAL_DARK -> NothingElevated
              IconPackStyle.SYSTEM_DEFAULT -> Color.Transparent
            }
          }
        )
        .border(
          width = 1.dp,
          color = if (iconPack != IconPackStyle.SYSTEM_DEFAULT) theme.border else Color.Transparent,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (app.icon != null) {
        val bitmap = remember(app.icon, iconPack, isDark) {
          drawableToBitmap(app.icon, iconPack == IconPackStyle.MONOCHROME, isDark)
        }
        Image(
          bitmap = bitmap.asImageBitmap(),
          contentDescription = app.label,
          modifier = Modifier
            .size(iconSize * 0.72f)
            .clip(CircleShape)
        )
      } else {
        // Fallback stylized Nothing glyph icon
        val iconVector = getIconVectorForApp(app.label)
        Icon(
          imageVector = iconVector,
          contentDescription = app.label,
          tint = if (!isDark) Color(0xFF0A0A0A) else (if (iconPack == IconPackStyle.MONOCHROME) NothingWhite else accentColor),
          modifier = Modifier.size(iconSize * 0.55f)
        )
      }

      // Notification Dot / Badge (Nothing OS Signature style)
      if (app.notificationCount > 0) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(2.dp)
            .clip(CircleShape)
            .background(accentColor)
            .border(1.dp, if (isDark) NothingBlack else Color.White, CircleShape)
            .padding(horizontal = if (app.notificationCount > 1) 4.dp else 0.dp),
          contentAlignment = Alignment.Center
        ) {
          if (app.notificationCount > 1) {
            Text(
              text = if (app.notificationCount > 99) "99+" else app.notificationCount.toString(),
              color = NothingWhite,
              fontSize = 8.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          } else {
            Box(modifier = Modifier.size(7.dp))
          }
        }
      } else if (app.isPinned) {
        // Small Nothing Red indicator dot if pinned
        Box(
          modifier = Modifier
            .size(6.dp)
            .align(Alignment.TopEnd)
            .padding(1.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }
    }

    if (showLabel) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = app.label,
        color = theme.textPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )
    }
  }
}

private fun drawableToBitmap(drawable: Drawable, applyGrayscale: Boolean, isDark: Boolean): Bitmap {
  val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
  val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  if (applyGrayscale) {
    val paint = Paint()
    val matrix = ColorMatrix().apply {
      setSaturation(0f)
      if (!isDark) {
        // Theme Jour (Light): Invert & boost contrast so light/white symbols become sharp deep black!
        val invertAndContrast = ColorMatrix(floatArrayOf(
          -1.4f, 0f, 0f, 0f, 245f,
          0f, -1.4f, 0f, 0f, 245f,
          0f, 0f, -1.4f, 0f, 245f,
          0f, 0f, 0f, 1f, 0f
        ))
        postConcat(invertAndContrast)
      } else {
        // Theme Nuit (Dark): Boost contrast for sharp white/grey glyphs
        val contrast = 1.25f
        val scale = FloatArray(20) { 0f }.apply {
          this[0] = contrast
          this[6] = contrast
          this[12] = contrast
          this[18] = 1f
        }
        postConcat(ColorMatrix(scale))
      }
    }
    paint.colorFilter = ColorMatrixColorFilter(matrix)
    val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    canvas.restoreToCount(layer)
  } else {
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
  }
  return bitmap
}

private fun getIconVectorForApp(name: String): ImageVector {
  return when {
    name.contains("Phone", true) || name.contains("Call", true) -> Icons.Default.Call
    name.contains("Message", true) || name.contains("Mail", true) -> Icons.Default.Email
    name.contains("Camera", true) -> Icons.Default.CameraAlt
    name.contains("Photo", true) || name.contains("Gallery", true) -> Icons.Default.Image
    name.contains("Chrome", true) || name.contains("Browser", true) -> Icons.Default.Language
    name.contains("Setting", true) -> Icons.Default.Settings
    name.contains("Music", true) || name.contains("Audio", true) -> Icons.Default.MusicNote
    name.contains("YouTube", true) || name.contains("Video", true) -> Icons.Default.PlayArrow
    name.contains("Map", true) -> Icons.Default.Map
    name.contains("File", true) -> Icons.Default.Folder
    else -> Icons.Default.Android
  }
}
