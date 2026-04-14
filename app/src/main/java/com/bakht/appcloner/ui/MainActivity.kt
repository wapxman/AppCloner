package com.bakht.appcloner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bakht.appcloner.AppClonerApp
import com.bakht.appcloner.R
import com.bakht.appcloner.model.AppInfo
import com.bakht.appcloner.model.ClonedApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerClones: RecyclerView
    private lateinit var recyclerApps: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: View
    private lateinit var searchEdit: EditText
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var cloneAdapter: ClonedAppAdapter
    private lateinit var appAdapter: AppListAdapter
    private var allApps: List<AppInfo> = emptyList()
    private var clonedApps: List<ClonedApp> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestNotificationPermission(); initViews(); setupTabs(); setupSearch(); loadData()
    }
    override fun onResume() { super.onResume(); refreshClones() }
    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout); recyclerClones = findViewById(R.id.recyclerClones)
        recyclerApps = findViewById(R.id.recyclerApps); progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView); searchEdit = findViewById(R.id.searchEdit)
        fabAdd = findViewById(R.id.fabAdd)
        cloneAdapter = ClonedAppAdapter({ clone -> AppClonerApp.instance.cloneManager.launchClone(clone) }, { clone -> confirmDeleteClone(clone) })
        recyclerClones.layoutManager = GridLayoutManager(this, 4); recyclerClones.adapter = cloneAdapter
        appAdapter = AppListAdapter { showCloneDialog(it) }
        recyclerApps.layoutManager = LinearLayoutManager(this); recyclerApps.adapter = appAdapter
        fabAdd.setOnClickListener { tabLayout.getTabAt(1)?.select() }
    }
    private fun setupTabs() { tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) { when (tab.position) { 0 -> showClones(); 1 -> showAllApps() } }
        override fun onTabUnselected(tab: TabLayout.Tab) {}; override fun onTabReselected(tab: TabLayout.Tab) {} }) }
    private fun setupSearch() { searchEdit.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { filterApps(s?.toString() ?: "") } }) }
    private fun showClones() { recyclerClones.visibility = View.VISIBLE; recyclerApps.visibility = View.GONE; searchEdit.visibility = View.GONE; fabAdd.visibility = View.VISIBLE; refreshClones() }
    private fun showAllApps() { recyclerClones.visibility = View.GONE; recyclerApps.visibility = View.VISIBLE; searchEdit.visibility = View.VISIBLE; fabAdd.visibility = View.GONE }
    private fun loadData() { progressBar.visibility = View.VISIBLE; lifecycleScope.launch { allApps = AppClonerApp.instance.cloneManager.getInstalledApps(); appAdapter.submitList(allApps); progressBar.visibility = View.GONE; refreshClones() } }
    private fun refreshClones() {
        clonedApps = AppClonerApp.instance.cloneManager.getClonedApps(); cloneAdapter.submitList(clonedApps)
        if (tabLayout.selectedTabPosition == 0) { emptyView.visibility = if (clonedApps.isEmpty()) View.VISIBLE else View.GONE; recyclerClones.visibility = if (clonedApps.isEmpty()) View.GONE else View.VISIBLE } else { emptyView.visibility = View.GONE }
    }
    private fun filterApps(query: String) { appAdapter.submitList(if (query.isBlank()) allApps else allApps.filter { it.appName.contains(query, true) || it.packageName.contains(query, true) }) }
    private fun showCloneDialog(appInfo: AppInfo) {
        val existing = AppClonerApp.instance.cloneManager.getClonesForPackage(appInfo.packageName)
        AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle("Clone ${appInfo.appName}?")
            .setMessage(if (existing.isEmpty()) "A copy will be created on a separate workspace." else "You already have ${existing.size} clone(s). Create another?")
            .setPositiveButton("Clone") { _, _ -> val clone = AppClonerApp.instance.cloneManager.cloneApp(appInfo); Toast.makeText(this, "${clone.cloneName} created!", Toast.LENGTH_SHORT).show(); tabLayout.getTabAt(0)?.select(); refreshClones() }
            .setNegativeButton("Cancel", null).show()
    }
    private fun confirmDeleteClone(clone: ClonedApp) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle("Delete clone?").setMessage("Delete ${clone.cloneName}?")
            .setPositiveButton("Delete") { _, _ -> AppClonerApp.instance.cloneManager.removeClone(clone.id); Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show(); refreshClones() }
            .setNegativeButton("Cancel", null).show()
    }
    private fun requestNotificationPermission() { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001) }
}
