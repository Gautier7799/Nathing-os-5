package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingMatteBlack
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AppDrawerSheet(
  apps: List<AppItem>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onAppClick: (AppItem) -> Unit,
  onTogglePin: (AppItem) -> Unit,
  onToggleDock: (AppItem) -> Unit,
  onOpenAppInfo: (AppItem) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed
) {
  var selectedAppForMenu by remember { mutableStateOf<AppItem?>(null) }
  val gridState = rememberLazyGridState()
  val scope = rememberCoroutineScope()

  val filteredApps by remember(apps, searchQuery) {
    derivedStateOf {
      if (searchQuery.isBlank()) {
        apps
      } else {
        apps.filter { it.label.contains(searchQuery.trim(), ignoreCase = true) }
      }
    }
  }

  // Available Alphabet headers for fast scroll
  val alphabetLetters = remember(apps) {
    apps.mapNotNull { it.label.firstOrNull()?.uppercaseChar() }
      .filter { it in 'A'..'Z' }
      .distinct()
      .sorted()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingBlack)
      .testTag("app_drawer_container")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
      // Top Bar: Back button + Search Box + Dot Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("close_drawer_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Home",
            tint = NothingWhite
          )
        }

        // Nothing OS Dot-Matrix Style Search Input
        Row(
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(NothingDarkSurface)
            .border(1.dp, NothingBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )

          Spacer(modifier = Modifier.width(10.dp))

          Box(modifier = Modifier.weight(1f)) {
            if (searchQuery.isEmpty()) {
              Text(
                text = "SEARCH APPS...",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = NothingGrey,
                letterSpacing = 1.sp
              )
            }
            BasicTextField(
              value = searchQuery,
              onValueChange = onSearchChange,
              textStyle = TextStyle(
                color = NothingWhite,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
              ),
              cursorBrush = SolidColor(accentColor),
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("app_search_input")
            )
          }

          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { onSearchChange("") },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = NothingGrey,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Drawer Content: Grid + Fast-Scroll Alphabet Sidebar
      Row(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(4),
          state = gridState,
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .testTag("app_drawer_grid")
        ) {
          items(filteredApps, key = { it.packageName }) { app ->
            AppIconItem(
              app = app,
              onClick = { onAppClick(app) },
              onLongClick = { selectedAppForMenu = app },
              iconSize = 56.dp,
              showLabel = true,
              iconPack = iconPack,
              accentColor = accentColor
            )
          }
        }

        // Fast Alphabet Scroller Rail
        if (searchQuery.isEmpty() && alphabetLetters.isNotEmpty()) {
          Column(
            modifier = Modifier
              .width(20.dp)
              .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            alphabetLetters.forEach { letter ->
              Text(
                text = letter.toString(),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NothingGrey,
                modifier = Modifier
                  .clickable {
                    val targetIdx = filteredApps.indexOfFirst {
                      it.label.startsWith(letter, ignoreCase = true)
                    }
                    if (targetIdx >= 0) {
                      scope.launch { gridState.animateScrollToItem(targetIdx) }
                    }
                  }
                  .padding(vertical = 1.dp)
              )
            }
          }
        }
      }
    }

    // App Long-Press Action Sheet
    selectedAppForMenu?.let { app ->
      AppContextMenuSheet(
        app = app,
        onDismiss = { selectedAppForMenu = null },
        onLaunch = {
          onAppClick(app)
          selectedAppForMenu = null
        },
        onTogglePin = {
          onTogglePin(app)
          selectedAppForMenu = null
        },
        onToggleDock = {
          onToggleDock(app)
          selectedAppForMenu = null
        },
        onAppInfo = {
          onOpenAppInfo(app)
          selectedAppForMenu = null
        },
        accentColor = accentColor
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContextMenuSheet(
  app: AppItem,
  onDismiss: () -> Unit,
  onLaunch: () -> Unit,
  onTogglePin: () -> Unit,
  onToggleDock: () -> Unit,
  onAppInfo: () -> Unit,
  accentColor: Color
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = NothingMatteBlack,
    contentColor = NothingWhite
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .testTag("app_context_menu")
    ) {
      // Header with App name
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
        Text(
          text = app.label.uppercase(Locale.ROOT),
          fontFamily = FontFamily.Monospace,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action 1: Open
      MenuRow(
        title = "OPEN APP",
        icon = Icons.AutoMirrored.Filled.OpenInNew,
        accentColor = accentColor,
        onClick = onLaunch
      )

      // Action 2: Pin / Unpin Home
      MenuRow(
        title = if (app.isPinned) "REMOVE FROM HOME" else "PIN TO HOME SCREEN",
        icon = Icons.Default.PushPin,
        accentColor = accentColor,
        onClick = onTogglePin
      )

      // Action 3: Add to Dock
      MenuRow(
        title = if (app.isDock) "REMOVE FROM DOCK" else "ADD TO DOCK FAVORITES",
        icon = Icons.Default.Star,
        accentColor = accentColor,
        onClick = onToggleDock
      )

      // Action 4: App Info
      MenuRow(
        title = "APP INFO & PERMISSIONS",
        icon = Icons.Default.Info,
        accentColor = accentColor,
        onClick = onAppInfo
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun MenuRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp, horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = accentColor,
      modifier = Modifier.size(20.dp)
    )
    Text(
      text = title,
      fontFamily = FontFamily.Monospace,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium,
      color = NothingWhite
    )
  }
}
