package com.bakht.appcloner.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bakht.appcloner.AppClonerApp
import com.bakht.appcloner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AppDetailActivity : AppCompatActivity() {
    private var packageName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        packageName = intent.getStringExtra("package_name") ?: run { finish(); return }
        val appInfo = try { val pm = packageManager; val info = pm.getApplicationInfo(packageName, 0); Pair(info.loadLabel(pm).toString(), info.loadIcon(pm)) } catch (e: Exception) { Pair(packageName, null) }
        findViewById<TextView>(R.id.tvAppName).text = appInfo.first
        findViewById<TextView>(R.id.tvPackageName).text = packageName
        appInfo.second?.let { findViewById<ImageView>(R.id.ivAppIcon).setImageDrawable(it) }
        val recycler = findViewById<RecyclerView>(R.id.recyclerClones)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ClonedAppAdapter(
            onLaunch = { clone -> AppClonerApp.instance.cloneManager.launchClone(clone) },
            onDelete = { clone -> AppClonerApp.instance.cloneManager.removeClone(clone.id); Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show(); refreshClones(recycler) }
        )
        refreshClones(recycler)
        findViewById<FloatingActionButton>(R.id.fabAddClone).setOnClickListener {
            val cm = AppClonerApp.instance.cloneManager
            if (cm.getClonesForPackage(packageName).size >= 5) { Toast.makeText(this, "Max 5 clones", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            cm.cloneApp(com.bakht.appcloner.model.AppInfo(packageName, appInfo.first, appInfo.second))
            Toast.makeText(this, "Clone created!", Toast.LENGTH_SHORT).show(); refreshClones(recycler)
        }
        findViewById<ImageView>(R.id.btnBack)?.setOnClickListener { finish() }
    }
    private fun refreshClones(recycler: RecyclerView) { (recycler.adapter as? ClonedAppAdapter)?.submitList(AppClonerApp.instance.cloneManager.getClonesForPackage(packageName)) }
}
