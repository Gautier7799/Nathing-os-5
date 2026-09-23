android {
    // الحل للخطأ الأول: وضع الـ namespace هنا بدلاً من المانيفست
    namespace 'com.example.nothinglauncher' 
    compileSdk 34

    defaultConfig {
        applicationId "com.example.nothinglauncher"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    // ... بقية الإعدادات
}

dependencies {
    // الحل للخطأ الثاني: إضافة مكتبة Coil لتحميل الصور
    implementation("io.coil-kt:coil-compose:2.4.0")
    
    // تأكد من وجود مكتبات Compose الأساسية
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
}
