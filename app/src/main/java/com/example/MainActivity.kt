package com.example.nothinglauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// استيراد مكتبة Coil (حل الخطأ الثاني)
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // تعريف حالة (State) للتحكم في ظهور العناصر (حل الخطأ الثالث - السطر 112)
    var showDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // استخدام Coil بشكل صحيح لتحميل صورة
        val painter = rememberAsyncImagePainter("https://example.com/image.jpg")
        
        Text(text = "Welcome to Nothing Launcher")
        
        Button(onClick = { 
            // ✅ الصح: نغير قيمة الحالة فقط، ولا نستدعي Composable هنا
            showDetails = true 
        }) {
            Text("Show Details")
        }

        // ✅ الصح: الاستدعاء يتم هنا في نطاق Composable بناءً على الحالة
        if (showDetails) {
            DetailSection()
        }
    }
}

@Composable
fun DetailSection() {
    Text(text = "This is the detailed information section!")
}
