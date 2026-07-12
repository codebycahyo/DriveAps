package com.example.kendaraanbp1.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.model.ReminderItem
import com.example.kendaraanbp1.data.model.ReminderSeverity
import com.example.kendaraanbp1.databinding.ItemReminderBinding

class ReminderAdapter : ListAdapter<ReminderItem, ReminderAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReminderItem) {
            val context = binding.root.context
            binding.reminderTitle.text = item.title
            binding.reminderSubtitle.text = item.subtitle
            binding.reminderIcon.setImageResource(item.iconRes)

            val isUrgent = item.severity == ReminderSeverity.URGENT
            val cardBg = if (isUrgent) R.drawable.bg_reminder_urgent else R.drawable.bg_reminder_standard
            val accentColor = if (isUrgent) R.color.reminder_urgent_accent else R.color.reminder_standard_accent
            val titleColor = if (isUrgent) R.color.reminder_urgent_title else R.color.reminder_standard_title
            val subtitleColor = if (isUrgent) R.color.reminder_urgent_subtitle else R.color.reminder_standard_subtitle

            binding.reminderCardBg.setBackgroundResource(cardBg)
            binding.reminderAccentBar.setBackgroundColor(ContextCompat.getColor(context, accentColor))
            binding.reminderIcon.imageTintList = ContextCompat.getColorStateList(context, accentColor)
            binding.reminderTitle.setTextColor(ContextCompat.getColor(context, titleColor))
            binding.reminderSubtitle.setTextColor(ContextCompat.getColor(context, subtitleColor))
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ReminderItem>() {
            override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem) =
                oldItem == newItem
        }
    }
}
