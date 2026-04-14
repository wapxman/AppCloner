package com.bakht.appcloner

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.bakht.appcloner.engine.VirtualEngine

class App : Application() {

    lateinit var engine: VirtualEngine
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        engine = VirtualEngine(this)
        engine.initialize()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
