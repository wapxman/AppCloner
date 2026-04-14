package com.bakht.appcloner.engine

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.bakht.appcloner.model.AppInfo
import com.bakht.appcloner.model.ClonedApp
import com.lody.virtual.client.core.VirtualCore
import com.lody.virtual.client.ipc.VClientImpl
import com.lody.virtual.remote.InstalledAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Core engine wrapping VirtualApp framework.
 * Handles initialization, app installation into virtual container,
 * launching clones in isolated processes, and cleanup.
 */
class VirtualEngine(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /**
     * Initialize the virtual environment.
     * Must be called in Application.onCreate()
     */
    fun initialize() {
        val virtualCore = VirtualCore.get()
        // Startup the virtual engine
        virtualCore.startup(context)
    }

    /**
     * Get list of all installed user apps on the real device.
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(0)
        }

        packages
            .filter { pkg ->
                val flags = pkg.applicationInfo?.flags ?: 0
                (flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }
            .filter { it.packageName != context.packageName }
            .map { pkg ->
                val ai = pkg.applicationInfo
                AppInfo(
                    packageName = pkg.packageName,
                    appName = ai?.loadLabel(pm)?.toString() ?: pkg.packageName,
                    icon = try { ai?.loadIcon(pm) } catch (e: Exception) { null },
                    versionName = pkg.versionName ?: "",
                    apkPath = ai?.sourceDir ?: ""
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    /**
     * Install an app into the virtual container (clone it).
     * This creates a fully isolated copy with its own data directory.
     *
     * @param packageName The package name of the app to clone
     * @param userId Virtual user ID (0 for first clone, 1 for second, etc.)
     * @return true if installation succeeded
     */
    suspend fun installClone(packageName: String, userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val apkPath = appInfo.sourceDir

            // Install into virtual environment
            val result = VirtualCore.get().installPackage(apkPath, 0)
            result.isSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Launch a cloned app in its isolated virtual environment.
     *
     * @param packageName Package name of the cloned app
     * @param userId Virtual user ID
     */
    fun launchClone(packageName: String, userId: Int = 0) {
        try {
            val intent = VirtualCore.get().getLaunchIntent(packageName, userId)
            if (intent != null) {
                VirtualCore.get().context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Uninstall a cloned app from the virtual container.
     * Removes all cloned app data.
     *
     * @param packageName Package name to remove
     * @param userId Virtual user ID
     */
    suspend fun removeClone(packageName: String, userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        try {
            VirtualCore.get().uninstallPackage(packageName)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get all apps installed in the virtual container.
     */
    fun getClonedApps(): List<ClonedApp> {
        return try {
            val installed = VirtualCore.get().getInstalledApps(0)
            installed.map { info ->
                val icon: Drawable? = try {
                    pm.getApplicationInfo(info.packageName, 0).loadIcon(pm)
                } catch (e: Exception) { null }

                val name = try {
                    pm.getApplicationInfo(info.packageName, 0).loadLabel(pm).toString()
                } catch (e: Exception) { info.packageName }

                ClonedApp(
                    packageName = info.packageName,
                    appName = name,
                    icon = icon,
                    userId = 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Check if a package is already cloned.
     */
    fun isCloned(packageName: String): Boolean {
        return try {
            VirtualCore.get().isAppInstalled(packageName)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Clear data of a cloned app.
     */
    suspend fun clearCloneData(packageName: String, userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        try {
            VirtualCore.get().clearPackage(packageName)
            true
        } catch (e: Exception) {
            false
        }
    }
}
