package com.bakht.appcloner.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String = "",
    val apkPath: String = ""
)

data class ClonedApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val userId: Int = 0
)
