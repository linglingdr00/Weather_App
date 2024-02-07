package com.linglingdr00.weather.ui.now

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.data.NowItem
import com.linglingdr00.weather.databinding.ListNowItemBinding

class NowAdapter: ListAdapter<NowItem, NowAdapter.NowViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<NowItem>() {
        override fun areItemsTheSame(oldItem: NowItem, newItem: NowItem): Boolean {
            return oldItem.townText == newItem.townText
        }

        override fun areContentsTheSame(oldItem: NowItem, newItem: NowItem): Boolean {
            return oldItem.townText == newItem.townText
        }
    }

    class NowViewHolder(private var binding: ListNowItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            fun bind(nowItem: NowItem) {
                binding.nowItem = nowItem
                binding.executePendingBindings()
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowViewHolder {
        val itemView = ListNowItemBinding.inflate(LayoutInflater.from(parent.context))
        return NowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NowViewHolder, position: Int) {
        // 取得與目前 RecyclerView 位置相關的 object
        val item = getItem(position)
        // 傳遞至 ForecastViewHolder 中的 bind() 方法
        holder.bind(item)
    }

}