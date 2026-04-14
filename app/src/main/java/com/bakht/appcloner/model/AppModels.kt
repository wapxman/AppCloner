package com.bakht.appcloner.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String = "",
    val isSystemApp: Boolean = false,
    val installedSize: Long = 0L
)

data class ClonedApp(
    val id: String,
    val originalPackage: String,
    val appName: String,
    val cloneIndex: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val workspaceName: String = "Workspace ${cloneIndex + 1}",
    val color: Int = 0
) {
    val cloneName: String get() = "$appName #${cloneIndex + 1}"
}

data class Workspace(
    val id: Int,
    val name: String,
    val color: Int,
    val clonedApps: MutableList<ClonedApp> = mutableListOf()
)
