package com.example.kendaraanbp1.ui.servicehistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.data.model.ServiceTimelineItem
import com.example.kendaraanbp1.databinding.ItemServiceTimelineBinding

class ServiceTimelineAdapter : ListAdapter<ServiceTimelineItem, ServiceTimelineAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isLast = position == itemCount - 1)
    }

    class ViewHolder(private val binding: ItemServiceTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServiceTimelineItem, isLast: Boolean) {
            binding.timelineIcon.setImageResource(item.iconRes)
            binding.timelineTitle.text = item.title
            binding.timelineSubtitle.text = item.workshopName
            binding.timelineStatus.text = item.statusLabel
            binding.timelineDateDistance.text = item.dateDistanceLabel
            binding.timelineCost.text = item.totalCostLabel
            binding.timelineLine.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ServiceTimelineItem>() {
            override fun areItemsTheSame(oldItem: ServiceTimelineItem, newItem: ServiceTimelineItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ServiceTimelineItem, newItem: ServiceTimelineItem) =
                oldItem == newItem
        }
    }
}
