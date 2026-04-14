package com.bakht.appcloner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bakht.appcloner.App
import com.bakht.appcloner.R
import com.bakht.appcloner.model.AppInfo

class AppListAdapter(
    private val onCloneClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppListAdapter.VH>(object : DiffUtil.ItemCallback<AppInfo>() {
    override fun areItemsTheSame(a: AppInfo, b: AppInfo) = a.packageName == b.packageName
    override fun areContentsTheSame(a: AppInfo, b: AppInfo) = a == b
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.ivIcon)
        private val name: TextView = view.findViewById(R.id.tvName)
        private val pkg: TextView = view.findViewById(R.id.tvPackage)
        private val badge: TextView = view.findViewById(R.id.tvCloneCount)
        private val btn: View = view.findViewById(R.id.btnClone)

        fun bind(app: AppInfo) {
            name.text = app.appName
            pkg.text = app.packageName
            app.icon?.let { icon.setImageDrawable(it) }

            val cloned = App.instance.engine.isCloned(app.packageName)
            if (cloned) {
                badge.visibility = View.VISIBLE
                badge.text = "CLONED"
            } else {
                badge.visibility = View.GONE
            }

            btn.setOnClickListener { onCloneClick(app) }
            itemView.setOnClickListener { onCloneClick(app) }
        }
    }
}
