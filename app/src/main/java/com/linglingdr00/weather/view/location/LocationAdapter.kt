package com.linglingdr00.weather.view.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.ListForecastItemBinding
import com.linglingdr00.weather.databinding.ListNowItemBinding
import com.linglingdr00.weather.model.data.ForecastItem
import com.linglingdr00.weather.model.data.LocationItem
import com.linglingdr00.weather.model.data.NowItem

class LocationAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var locationItemList: List<LocationItem> = listOf()
    // 更新資料的方法
    fun setLocationItem(itemList: List<LocationItem>) {
        locationItemList = itemList
    }

    companion object {
        private const val TYPE_NOW = 0
        private const val TYPE_FORECAST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            TYPE_NOW
        } else {
            TYPE_FORECAST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_NOW) {
            val binding = DataBindingUtil.inflate<ListNowItemBinding>(
                LayoutInflater.from(parent.context), R.layout.list_now_item, parent, false)
            NowViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ListForecastItemBinding>(
                LayoutInflater.from(parent.context), R.layout.list_forecast_item, parent, false)
            ForecastViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return locationItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = locationItemList[position]
        when (holder.itemViewType) {
            TYPE_NOW -> {
                holder as NowViewHolder
                (item.item) as NowItem
                holder.bind(item.item)
            }
            TYPE_FORECAST -> {
                holder as ForecastViewHolder
                (item.item) as ForecastItem
                holder.bind(item.item)
            }
            else -> {}
        }
    }

    inner class NowViewHolder(private var binding: ListNowItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nowItem: NowItem) {
            binding.nowItem = nowItem
            binding.executePendingBindings()
        }
    }

    inner class ForecastViewHolder(private var binding: ListForecastItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(forecastItem: ForecastItem) {
            //binding.viewModel = viewModel
            binding.forecastItem = forecastItem
            binding.executePendingBindings()
        }
    }

}