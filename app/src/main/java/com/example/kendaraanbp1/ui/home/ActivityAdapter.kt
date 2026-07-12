package com.example.kendaraanbp1.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.data.model.ActivityItem
import com.example.kendaraanbp1.databinding.ItemActivityBinding

class ActivityAdapter : ListAdapter<ActivityItem, ActivityAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ActivityItem) {
            binding.activityTitle.text = item.title
            binding.activitySubtitle.text = item.subtitle
            binding.activityAmount.text = item.amountLabel
            binding.activityIcon.setImageResource(item.iconRes)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ActivityItem>() {
            override fun areItemsTheSame(oldItem: ActivityItem, newItem: ActivityItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ActivityItem, newItem: ActivityItem) =
                oldItem == newItem
        }
    }
}
