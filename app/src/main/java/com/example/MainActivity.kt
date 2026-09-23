package com.example.nothinglauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import android.content.Context

// 1. Model
data class AppInfo(
    val label: CharSequence,
    val packageName: CharSequence,
    val icon: android.graphics.drawable.Drawable
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // جعل التطبيق يظهر كـ Launcher أساسي عند فتحه
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivity(homeIntent)

        setContent {
            MaterialTheme {
                val apps = remember { getInstalledApps(this) }
                NothingLauncherScreen(apps)
            }
        }
    }

    // 2. Logic to get apps
    private fun getInstalledApps(context: Context): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
        for (info in resolveInfos) {
            apps.add(
                AppInfo(
                    label = info.loadLabel(context.packageManager),
                    packageName = info.activityInfo.packageName,
                    icon = info.loadIcon(context.packageManager)
                )
            )
        }
        return apps.sortedBy { it.label.toString().lowercase() }
    }
}

// 3. UI Components
@Composable
fun NothingLauncherScreen(apps: List<AppInfo>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.Black
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(apps) { app ->
                AppItem(app) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                    context.startActivity(launchIntent)
                }
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = rememberAsyncImagePainter(app.icon),
            contentDescription = app.label.toString(),
            modifier = Modifier.size(55.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label.toString(),
            color = androidx.compose.ui.graphics.Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
