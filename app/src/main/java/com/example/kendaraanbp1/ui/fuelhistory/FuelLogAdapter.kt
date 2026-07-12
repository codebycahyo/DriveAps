package com.example.kendaraanbp1.ui.fuelhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.model.FuelLogItem
import com.example.kendaraanbp1.databinding.ItemFuelLogBinding

class FuelLogAdapter : ListAdapter<FuelLogItem, FuelLogAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFuelLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemFuelLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FuelLogItem) {
            binding.logIcon.setImageResource(R.drawable.ic_fuel)
            binding.logStationName.text = item.stationName
            binding.logDate.text = item.date
            binding.logAmount.text = item.amountLabel
            binding.logLiters.text = item.litersLabel
            binding.logOdometer.text = item.odometerLabel
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FuelLogItem>() {
            override fun areItemsTheSame(oldItem: FuelLogItem, newItem: FuelLogItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FuelLogItem, newItem: FuelLogItem) =
                oldItem == newItem
        }
    }
}
