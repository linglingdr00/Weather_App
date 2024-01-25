package com.linglingdr00.weather

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.ui.forecast.ForecastAdapter
import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.forecast.ForecastViewModel.WeatherApiStatus

/* @BindingAdapter 註解會通知 data binding */

//初始化 ForecastAdapter
@BindingAdapter("forecastData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ForecastItem>?) {
    // 將 recyclerView.adapter 做為 ForecastAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as ForecastAdapter
    // 呼叫 adapter.submitList() 可查看 ForecastItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

//根據 weatherCode 設定天氣圖片
@BindingAdapter("weatherImg")
fun setWeatherImg(imgView: ImageView, weatherCode: String?) {
    var imgRes: Int
    if (weatherCode == "1" || weatherCode == "24") {
        imgRes = R.drawable.sun
    } else if (weatherCode == "2" || weatherCode == "3" || weatherCode == "25" ||
        weatherCode == "26") {
        imgRes = R.drawable.suncloud
    } else if (weatherCode == "4" || weatherCode == "27") {
        imgRes = R.drawable.clouds
    } else if (weatherCode == "5" || weatherCode == "6" || weatherCode == "7"
        || weatherCode == "28") {
        imgRes = R.drawable.cloudy
    } else if (weatherCode == "8" || weatherCode == "9" || weatherCode == "10" ||
        weatherCode == "11" || weatherCode == "12" || weatherCode == "20" ||
        weatherCode == "31" || weatherCode == "38" || weatherCode == "39") {
        imgRes = R.drawable.rainy
    } else if (weatherCode == "13" || weatherCode == "14" || weatherCode == "29" ||
        weatherCode == "30" || weatherCode == "32") {
        imgRes = R.drawable.heavyrain
    } else if (weatherCode == "15" || weatherCode == "16" || weatherCode == "17" ||
        weatherCode == "18" || weatherCode == "22" || weatherCode == "33" ||
        weatherCode == "34" || weatherCode == "35" || weatherCode == "36" || weatherCode == "41") {
        imgRes = R.drawable.storm
    } else if (weatherCode == "19" || weatherCode == "21") {
        imgRes = R.drawable.suncloudrain
    } else if (weatherCode == "23" || weatherCode == "42") {
        imgRes = R.drawable.snow
    } else {
        imgRes = R.drawable.snowrain
    }
    imgView.setImageResource(imgRes)
}

//設定降雨機率文字
@BindingAdapter("popText")
fun setPopText(textView: TextView, weatherPop: String?) {
    weatherPop.let {
        textView.setText("降雨機率: $weatherPop%")
    }
}

/* 設定 status 以顯示 loading 和 error status 的 icon */
@BindingAdapter("weatherApiStatus")
fun bindStatus(imageView: ImageView, status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            // 將 status ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 loading animation
            imageView.setImageResource(R.drawable.loading_animation)
        }
        WeatherApiStatus.ERROR -> {
            // 將 state ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 connection error 圖示
            imageView.setImageResource(R.drawable.ic_connection_error)
        }
        WeatherApiStatus.DONE -> {
            // 將 state ImageView 設定為不可顯示
            imageView.visibility = View.GONE
        }
        else -> {}
    }
}