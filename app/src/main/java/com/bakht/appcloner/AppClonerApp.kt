package com.bakht.appcloner

import android.app.Application
import com.bakht.appcloner.utils.CloneManager
import com.bakht.appcloner.utils.PrefsManager

class AppClonerApp : Application() {
    lateinit var cloneManager: CloneManager
    lateinit var prefsManager: PrefsManager
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefsManager = PrefsManager(this)
        cloneManager = CloneManager(this)
    }
    companion object {
        lateinit var instance: AppClonerApp
            private set
    }
}
