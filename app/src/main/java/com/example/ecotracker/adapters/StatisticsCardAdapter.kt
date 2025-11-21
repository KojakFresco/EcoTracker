package com.example.ecotracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.databinding.ItemStatisticCardBinding

data class StatisticItem(
    val title: String,
    val iconResId: Int,
    val value: String,
    val subtitle: String
)

class StatisticsCardAdapter(private val items: List<StatisticItem>) :
    RecyclerView.Adapter<StatisticsCardAdapter.StatisticViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val binding = ItemStatisticCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatisticViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class StatisticViewHolder(private val binding: ItemStatisticCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StatisticItem) {
            binding.tvStatisticTitle.text = item.title
            binding.ivStatisticIcon.setImageResource(item.iconResId)
            binding.tvStatisticValue.text = item.value
            binding.tvStatisticSubtitle.text = item.subtitle
        }
    }
}