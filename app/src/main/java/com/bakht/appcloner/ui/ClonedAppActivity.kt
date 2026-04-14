package com.bakht.appcloner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bakht.appcloner.utils.CloneManager

class ClonedAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(CloneManager.EXTRA_PACKAGE_NAME)
        val cloneName = intent.getStringExtra(CloneManager.EXTRA_CLONE_NAME) ?: "Clone"
        val cloneIndex = intent.getIntExtra(CloneManager.EXTRA_CLONE_INDEX, 0)
        if (packageName == null) { Toast.makeText(this, "Error: package not found", Toast.LENGTH_SHORT).show(); finish(); return }
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    addCategory("clone_$cloneIndex")
                    putExtra("clone_workspace", cloneIndex); putExtra("clone_name", cloneName)
                }
                startActivity(launchIntent)
                Toast.makeText(this, "$cloneName launched", Toast.LENGTH_SHORT).show()
            } else { Toast.makeText(this, "Cannot launch $cloneName", Toast.LENGTH_LONG).show() }
        } catch (e: Exception) { Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show() }
        finish()
    }
}
