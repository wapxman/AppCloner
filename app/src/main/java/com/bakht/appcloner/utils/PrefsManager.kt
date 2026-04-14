package com.bakht.appcloner.utils

import android.content.Context
import android.content.SharedPreferences
import com.bakht.appcloner.model.ClonedApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_cloner_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    fun saveClonedApps(apps: List<ClonedApp>) { prefs.edit().putString(KEY_CLONED_APPS, gson.toJson(apps)).apply() }
    fun getClonedApps(): MutableList<ClonedApp> {
        val json = prefs.getString(KEY_CLONED_APPS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<ClonedApp>>() {}.type
        return try { gson.fromJson(json, type) ?: mutableListOf() } catch (e: Exception) { mutableListOf() }
    }
    fun getNextCloneIndex(packageName: String): Int {
        val clones = getClonedApps().filter { it.originalPackage == packageName }
        return if (clones.isEmpty()) 0 else clones.maxOf { it.cloneIndex } + 1
    }
    var lastSelectedWorkspace: Int
        get() = prefs.getInt(KEY_LAST_WORKSPACE, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_WORKSPACE, value).apply()
    companion object { private const val KEY_CLONED_APPS = "cloned_apps"; private const val KEY_LAST_WORKSPACE = "last_workspace" }
}
