package com.bakht.appcloner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bakht.appcloner.R
import com.bakht.appcloner.model.ClonedApp

class ClonedAppAdapter(private val onLaunch: (ClonedApp) -> Unit, private val onDelete: (ClonedApp) -> Unit) : ListAdapter<ClonedApp, ClonedAppAdapter.ViewHolder>(object : DiffUtil.ItemCallback<ClonedApp>() {
    override fun areItemsTheSame(a: ClonedApp, b: ClonedApp) = a.id == b.id
    override fun areContentsTheSame(a: ClonedApp, b: ClonedApp) = a == b
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cloned_app, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.ivCloneIcon)
        private val name: TextView = view.findViewById(R.id.tvCloneName)
        private val badge: TextView = view.findViewById(R.id.tvBadge)
        fun bind(clone: ClonedApp) {
            name.text = clone.appName; badge.text = "#${clone.cloneIndex + 1}"; badge.setBackgroundColor(clone.color)
            try { val pm = itemView.context.packageManager; icon.setImageDrawable(pm.getApplicationInfo(clone.originalPackage, 0).loadIcon(pm)) } catch (e: Exception) { icon.setImageResource(android.R.drawable.sym_def_app_icon) }
            itemView.setOnClickListener { onLaunch(clone) }; itemView.setOnLongClickListener { onDelete(clone); true }
        }
    }
}
