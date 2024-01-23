package com.linglingdr00.weathernotification

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weathernotification.model.ForecastItem
import com.linglingdr00.weathernotification.ui.forecast.ForecastAdapter

/* 初始化包含 ForecastItem list 的 ForecastAdapter */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ForecastItem>?) {
    // 將 recyclerView.adapter 做為 ForecastAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as ForecastAdapter
    // 呼叫 adapter.submitList() 可查看 ForecastItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

/* @BindingAdapter 註解會通知 data binding，
  在 View item 擁有 ImageView 的 imageUrl 屬性時執行此 binding adapter */

@BindingAdapter("imageId")
fun bindImage(imgView: ImageView, imgId: Int?) {
    imgId?.let {
        imgView.setImageResource(imgId)
    }
}

@BindingAdapter("popText")
fun setPopText(textView: TextView, weatherPop: String?) {
    weatherPop.let {
        textView.setText("降雨機率: $weatherPop%")
    }
}