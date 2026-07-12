package com.example.kendaraanbp1.ui.vehicledetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.data.model.VehicleActivityItem
import com.example.kendaraanbp1.databinding.ItemVehicleActivityBinding

class VehicleActivityAdapter : ListAdapter<VehicleActivityItem, VehicleActivityAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVehicleActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemVehicleActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VehicleActivityItem) {
            val context = binding.root.context
            binding.activityTitle.text = item.title
            binding.activityDate.text = item.date
            binding.activitySubtitle.text = item.subtitle
            binding.activityAmount.text = item.amountLabel
            binding.activityIcon.setImageResource(item.iconRes)
            binding.activityIcon.imageTintList = ContextCompat.getColorStateList(context, item.iconTintRes)
            (binding.activityIcon.parent as? android.widget.FrameLayout)?.backgroundTintList =
                ContextCompat.getColorStateList(context, item.iconBgRes)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<VehicleActivityItem>() {
            override fun areItemsTheSame(oldItem: VehicleActivityItem, newItem: VehicleActivityItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: VehicleActivityItem, newItem: VehicleActivityItem) =
                oldItem == newItem
        }
    }
}
