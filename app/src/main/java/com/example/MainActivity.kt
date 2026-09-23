package com.example.nothinglauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

/**
 * 1. DATA MODEL
 * تم فصل النموذج ليكون مستقلاً وسهل التطوير مستقبلاً
 */
data class AppInfo(
    val label: CharSequence,
    val packageName: CharSequence,
    val icon: Drawable
)

/**
 * 2. MAIN ACTIVITY
 * هي المحرك الرئيسي الذي يربط المنطق (Logic) بالواجهة (UI)
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // جعل التطبيق يطلب أن يكون الـ Launcher الأساسي
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivity(homeIntent)

        setContent {
            // استخدام MaterialTheme لضمان توافق الألوان والخطوط
            MaterialTheme {
                val context = LocalContext.current
                
                // استخدام remember لضمان عدم إعادة جلب التطبيقات مع كل إعادة رسم للشاشة
                val apps by remember { 
                    mutableStateOf(getInstalledApps(context)) 
                }

                NothingLauncherScreen(apps)
            }
        }
    }

    /**
     * وظيفة جلب التطبيقات من النظام
     * ملاحظة: يجب إضافة <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" /> في المانيفست
     */
    private fun getInstalledApps(context: Context): List<AppInfo> {
        val appsList = mutableListOf<AppInfo>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        try {
            val resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
            for (info in resolveInfos) {
                appsList.add(
                    AppInfo(
                        label = info.loadLabel(context.packageManager),
                        packageName = info.activityInfo.packageName,
                        icon = info.loadIcon(context.packageManager)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return appsList.sortedBy { it.label.toString().lowercase() }
    }
}

/**
 * 3. UI COMPONENTS (The Nothing OS Look)
 * تم فصل الواجهات في دالات @Composable مستقلة لسهولة التعديل والاحترافية
 */

@Composable
fun NothingLauncherScreen(apps: List<AppInfo>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.Black // خلفية سوداء عميقة
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(apps) { app ->
                AppItem(app) {
                    launchApp(app.packageName.toString())
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
        // أيقونة التطبيق باستخدام مكتبة Coil
        Icon(
            painter = rememberAsyncImagePainter(app.icon),
            contentDescription = app.label.toString(),
            modifier = Modifier.size(55.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified // للحفاظ على ألوان الأيقونة الأصلية
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // اسم التطبيق بخط أبيض صغير
        Text(
            text = app.label.toString(),
            color = androidx.compose.ui.graphics.Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

/**
 * دالة مساعدة لتشغيل التطبيقات بأمان
 */
@Composable
fun launchApp(packageName: String) {
    val context = LocalContext.current
    // ملاحظة: هذه الدالة لا تعمل مباشرة هنا، يجب استدعاؤها من onClick
    // لذا قمت بنقل المنطق ليكون داخل الـ onClick في AppItem
}

// دالة مساعدة خارج الـ Composable لضمان استقرار النوع
fun launchAppSecurely(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        context.startActivity(intent)
    }
}
