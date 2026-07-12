package com.example.kendaraanbp1.ui.documents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.model.DocumentItem
import com.example.kendaraanbp1.data.model.DocumentStatus
import com.example.kendaraanbp1.databinding.ItemDocumentBinding

class DocumentAdapter : ListAdapter<DocumentItem, DocumentAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentItem) {
            val context = binding.root.context
            binding.docTitle.text = item.title
            binding.docSubtitle.text = item.subtitle
            binding.docIcon.setImageResource(item.iconRes)
            binding.docStatusBadge.text = item.statusLabel

            val (bgRes, textRes) = when (item.status) {
                DocumentStatus.EXPIRING -> R.color.status_amber_bg to R.color.status_amber_text
                DocumentStatus.VERIFIED -> R.color.status_neutral_bg to R.color.neutral_600
                DocumentStatus.ACTIVE -> R.color.status_teal_bg to R.color.status_teal_text
                DocumentStatus.URGENT -> R.color.status_error_container to R.color.status_error_strong
            }
            binding.docStatusBadge.backgroundTintList = ContextCompat.getColorStateList(context, bgRes)
            binding.docStatusBadge.setTextColor(ContextCompat.getColor(context, textRes))
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DocumentItem>() {
            override fun areItemsTheSame(oldItem: DocumentItem, newItem: DocumentItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DocumentItem, newItem: DocumentItem) =
                oldItem == newItem
        }
    }
}
