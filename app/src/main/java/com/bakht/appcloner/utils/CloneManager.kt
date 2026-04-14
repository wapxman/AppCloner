package com.bakht.appcloner.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.bakht.appcloner.model.AppInfo
import com.bakht.appcloner.model.ClonedApp
import com.bakht.appcloner.ui.ClonedAppActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CloneManager(private val context: Context) {
    private val pm: PackageManager = context.packageManager
    private val prefsManager = PrefsManager(context)
    private val cloneColors = intArrayOf(
        Color.parseColor("#FF6B6B"), Color.parseColor("#4ECDC4"),
        Color.parseColor("#45B7D1"), Color.parseColor("#96CEB4"),
        Color.parseColor("#FFEAA7"), Color.parseColor("#DDA0DD"),
        Color.parseColor("#FF8C42"), Color.parseColor("#98D8C8")
    )
    suspend fun getInstalledApps(includeSystem: Boolean = false): List<AppInfo> = withContext(Dispatchers.IO) {
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else { @Suppress("DEPRECATION") pm.getInstalledPackages(0) }
        packages.filter { pkg -> if (includeSystem) true else (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) == 0) }
            .filter { it.packageName != context.packageName }
            .map { pkg -> val ai = pkg.applicationInfo; AppInfo(pkg.packageName, ai?.loadLabel(pm)?.toString() ?: pkg.packageName, try { ai?.loadIcon(pm) } catch (e: Exception) { null }, pkg.versionName ?: "", (ai?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0), 0L) }
            .sortedBy { it.appName.lowercase() }
    }
    fun cloneApp(appInfo: AppInfo, addShortcut: Boolean = true): ClonedApp {
        val index = prefsManager.getNextCloneIndex(appInfo.packageName)
        val clonedApp = ClonedApp(UUID.randomUUID().toString(), appInfo.packageName, appInfo.appName, index, color = cloneColors[index % cloneColors.size])
        val allClones = prefsManager.getClonedApps(); allClones.add(clonedApp); prefsManager.saveClonedApps(allClones)
        if (addShortcut) createShortcut(clonedApp, appInfo.icon)
        return clonedApp
    }
    fun removeClone(cloneId: String) {
        val allClones = prefsManager.getClonedApps(); allClones.removeAll { it.id == cloneId }; prefsManager.saveClonedApps(allClones)
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(cloneId))
    }
    fun getClonedApps(): List<ClonedApp> = prefsManager.getClonedApps()
    fun getClonesForPackage(packageName: String): List<ClonedApp> = prefsManager.getClonedApps().filter { it.originalPackage == packageName }
    fun launchClone(clonedApp: ClonedApp) {
        context.startActivity(Intent(context, ClonedAppActivity::class.java).apply {
            putExtra(EXTRA_CLONE_ID, clonedApp.id); putExtra(EXTRA_PACKAGE_NAME, clonedApp.originalPackage)
            putExtra(EXTRA_CLONE_INDEX, clonedApp.cloneIndex); putExtra(EXTRA_CLONE_NAME, clonedApp.cloneName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        })
    }
    private fun createShortcut(clonedApp: ClonedApp, originalIcon: Drawable?) {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) return
        val intent = Intent(context, ClonedAppActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(EXTRA_CLONE_ID, clonedApp.id); putExtra(EXTRA_PACKAGE_NAME, clonedApp.originalPackage)
            putExtra(EXTRA_CLONE_INDEX, clonedApp.cloneIndex); putExtra(EXTRA_CLONE_NAME, clonedApp.cloneName)
        }
        val icon = createBadgedIcon(originalIcon, clonedApp.cloneIndex, clonedApp.color)
        ShortcutManagerCompat.requestPinShortcut(context, ShortcutInfoCompat.Builder(context, clonedApp.id)
            .setShortLabel(clonedApp.cloneName).setLongLabel("${clonedApp.appName} - ${clonedApp.workspaceName}")
            .setIcon(icon).setIntent(intent).build(), null)
    }
    private fun createBadgedIcon(original: Drawable?, index: Int, color: Int): IconCompat {
        val size = 192; val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888); val canvas = Canvas(bitmap)
        original?.let { it.setBounds(0, 0, size, size); it.draw(canvas) }
        val bs = size * 0.35f
        canvas.drawCircle(size - bs/2 - 4, bs/2 + 4, bs/2, Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.FILL; setShadowLayer(4f, 0f, 2f, Color.argb(80,0,0,0)) })
        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = Color.WHITE; textSize = bs * 0.6f; textAlign = Paint.Align.CENTER; isFakeBoldText = true }
        canvas.drawText("${index+1}", size - bs/2 - 4, bs/2 + 4 + tp.textSize/3, tp)
        return IconCompat.createWithBitmap(bitmap)
    }
    companion object { const val EXTRA_CLONE_ID = "clone_id"; const val EXTRA_PACKAGE_NAME = "package_name"; const val EXTRA_CLONE_INDEX = "clone_index"; const val EXTRA_CLONE_NAME = "clone_name" }
}
