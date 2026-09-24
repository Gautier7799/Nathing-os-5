package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.model.LauncherSettings
import com.example.ui.theme.NothingBlack
import java.io.File

@Composable
fun NothingWallpaperBackground(
  settings: LauncherSettings,
  isLockScreen: Boolean = false,
  accentColor: Color,
  onDoubleTap: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val effectiveIndex = if (isLockScreen && settings.lockScreenWallpaperIndex >= 0) {
    settings.lockScreenWallpaperIndex
  } else {
    settings.wallpaperIndex
  }

  val effectiveUri = if (isLockScreen && settings.lockScreenWallpaperIndex >= 0) {
    settings.customLockScreenWallpaperUri ?: settings.customWallpaperUri
  } else {
    settings.customWallpaperUri
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingBlack)
      .testTag(if (isLockScreen) "lock_wallpaper_bg" else "home_wallpaper_bg")
  ) {
    // 1. Wallpaper Layer
    when (effectiveIndex) {
      0 -> {
        // DOT MATRIX NOIR
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
          val dotSpacing = 30f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }

      1 -> {
        // CARBON MATTE (Pure deep Nothing stealth)
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
        )
      }

      2 -> {
        // RED CIRCUIT GLOW
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
          val w = size.width
          val h = size.height
          drawLine(accentColor, Offset(0f, h * 0.22f), Offset(w * 0.45f, h * 0.22f), strokeWidth = 2.5f)
          drawLine(accentColor, Offset(w * 0.45f, h * 0.22f), Offset(w * 0.7f, h * 0.36f), strokeWidth = 2.5f)
          drawLine(accentColor, Offset(w * 0.7f, h * 0.36f), Offset(w, h * 0.36f), strokeWidth = 2.5f)
          drawCircle(accentColor, 5f, Offset(w * 0.45f, h * 0.22f))
          drawCircle(accentColor, 5f, Offset(w * 0.7f, h * 0.36f))

          drawLine(accentColor, Offset(w * 0.15f, h), Offset(w * 0.15f, h * 0.65f), strokeWidth = 2f)
          drawLine(accentColor, Offset(w * 0.15f, h * 0.65f), Offset(w * 0.55f, h * 0.55f), strokeWidth = 2f)
          drawCircle(accentColor, 4.5f, Offset(w * 0.15f, h * 0.65f))
          drawCircle(accentColor, 4.5f, Offset(w * 0.55f, h * 0.55f))
        }
      }

      3 -> {
        // LIGHT MONOCHROME
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.18f)) {
          val dotSpacing = 24f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.4f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }

      4 -> {
        // NOTHING GLYPH SIGNATURE (Asset image)
        Image(
          painter = painterResource(id = R.drawable.img_nothing_wallpaper),
          contentDescription = "Nothing Glyph Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }

      5 -> {
        // RED ECLIPSE AURA (Asset image)
        Image(
          painter = painterResource(id = R.drawable.img_nothing_red_wallpaper),
          contentDescription = "Nothing Red Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }

      6 -> {
        // RETRO WIREFRAME GRID
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.14f)) {
          val w = size.width
          val h = size.height
          val stepX = w / 8f
          val stepY = h / 16f
          for (i in 0..8) {
            drawLine(accentColor, Offset(i * stepX, 0f), Offset(i * stepX, h), strokeWidth = 1f)
          }
          for (j in 0..16) {
            drawLine(accentColor, Offset(0f, j * stepY), Offset(w, j * stepY), strokeWidth = 1f)
          }
        }
      }

      7 -> {
        // CUSTOM PHOTO / GALLERY WALLPAPER
        if (!effectiveUri.isNullOrBlank()) {
          val file = File(effectiveUri)
          if (file.exists()) {
            AsyncImage(
              model = file,
              contentDescription = "Custom Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          } else {
            AsyncImage(
              model = effectiveUri,
              contentDescription = "Custom Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        } else {
          // Fallback if no custom photo picked yet
          Image(
            painter = painterResource(id = R.drawable.img_nothing_wallpaper),
            contentDescription = "Default Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
      }

      else -> {
        // Fallback to Dot Matrix
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
          val dotSpacing = 30f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }
    }

    // 2. Dim Scrim Overlay (Crucial for high legibility of icons and clock over any wallpaper photo)
    val dimAlpha = (settings.wallpaperDimPct / 100f).coerceIn(0f, 0.85f)
    if (dimAlpha > 0f && (effectiveIndex == 4 || effectiveIndex == 5 || effectiveIndex == 7)) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = dimAlpha))
      )
    }

    // 3. Double-tap background detector (non-interfering)
    if (onDoubleTap != null && settings.doubleTapToSleep) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectTapGestures(
              onDoubleTap = { onDoubleTap() }
            )
          }
      )
    }
  }
}
