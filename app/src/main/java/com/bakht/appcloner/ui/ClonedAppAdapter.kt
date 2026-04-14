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

class ClonedAppAdapter(
    private val onLaunch: (ClonedApp) -> Unit,
    private val onDelete: (ClonedApp) -> Unit
) : ListAdapter<ClonedApp, ClonedAppAdapter.VH>(object : DiffUtil.ItemCallback<ClonedApp>() {
    override fun areItemsTheSame(a: ClonedApp, b: ClonedApp) = a.packageName == b.packageName
    override fun areContentsTheSame(a: ClonedApp, b: ClonedApp) = a == b
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_cloned_app, parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.ivCloneIcon)
        private val name: TextView = view.findViewById(R.id.tvCloneName)
        private val badge: TextView = view.findViewById(R.id.tvBadge)

        fun bind(clone: ClonedApp) {
            name.text = clone.appName
            badge.text = "#${clone.userId + 1}"
            clone.icon?.let { icon.setImageDrawable(it) }
                ?: icon.setImageResource(android.R.drawable.sym_def_app_icon)

            itemView.setOnClickListener { onLaunch(clone) }
            itemView.setOnLongClickListener { onDelete(clone); true }
        }
    }
}
