package com.linglingdr00.weather.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.databinding.ListForecastItemBinding

class ForecastAdapter: ListAdapter<ForecastItem, ForecastAdapter.ForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
        override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.locationText == newItem.locationText
        }

        override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.locationText == newItem.locationText
        }
    }

    class ForecastViewHolder(private var binding: ListForecastItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(forecastItem: ForecastItem) {
            //binding.viewModel = viewModel
            binding.forecastItem = forecastItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val itemView = ListForecastItemBinding.inflate(LayoutInflater.from(parent.context))
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        // 取得與目前 RecyclerView 位置相關的 object
        val item = getItem(position)
        // 傳遞至 ForecastViewHolder 中的 bind() 方法
        holder.bind(item)
    }

}