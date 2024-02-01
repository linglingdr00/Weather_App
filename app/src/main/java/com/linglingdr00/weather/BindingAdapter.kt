package com.linglingdr00.weather

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.ui.forecast.ForecastAdapter
import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.forecast.ForecastViewModel.ForecastWeatherApiStatus
import com.linglingdr00.weather.ui.location.LocationForecastAdapter
import com.linglingdr00.weather.ui.location.LocationNowAdapter
import com.linglingdr00.weather.ui.now.NowAdapter
import com.linglingdr00.weather.ui.now.NowItem
import com.linglingdr00.weather.ui.now.NowViewModel.NowWeatherApiStatus

/* @BindingAdapter 註解會通知 data binding */

//初始化 ForecastAdapter
@BindingAdapter("forecastData")
fun bindForecastRecyclerView(recyclerView: RecyclerView, data: List<ForecastItem>?) {
    // 將 recyclerView.adapter 做為 ForecastAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as ForecastAdapter
    // 呼叫 adapter.submitList() 可查看 ForecastItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

//初始化 NowAdapter
@BindingAdapter("nowData")
fun bindNowRecyclerView(recyclerView: RecyclerView, data: List<NowItem>?) {
    // 將 recyclerView.adapter 做為 NowAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as NowAdapter
    // 呼叫 adapter.submitList() 可查看 NowItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

//初始化 LocationForecastAdapter
@BindingAdapter("locationForecastData")
fun bindLocationForecastRecyclerView(recyclerView: RecyclerView, data: List<ForecastItem>?) {
    // 將 recyclerView.adapter 做為 LocationForecastAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as LocationForecastAdapter
    // 呼叫 adapter.submitList() 可查看 ForecastItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

//初始化 NowForecastAdapter
@BindingAdapter("locationNowData")
fun bindLocationNowRecyclerView(recyclerView: RecyclerView, data: List<NowItem>?) {
    // 將 recyclerView.adapter 做為 LocationNowAdapter 並指派給新的 val 屬性 adapter
    val adapter = recyclerView.adapter as LocationNowAdapter
    // 呼叫 adapter.submitList() 可查看 NowItem list data (出現新的 list 時，這個屬性會通知 RecyclerView)
    adapter.submitList(data)
}

//根據 weatherCode 設定天氣圖片
@BindingAdapter("forecastWeatherImg")
fun setForecastWeatherImg(imgView: ImageView, weatherCode: String?) {
    val imgRes: Int
    when (weatherCode) {
        "1", "24" -> {
            imgRes = R.drawable.sun
        }
        "2", "3", "25", "26" -> {
            imgRes = R.drawable.suncloud
        }
        "4", "27" -> {
            imgRes = R.drawable.clouds
        }
        "5", "6", "7", "28" -> {
            imgRes = R.drawable.cloudy
        }
        "8", "9", "10", "11", "12", "20", "31", "38", "39" -> {
            imgRes = R.drawable.rainy
        }
        "13", "14", "29", "30", "32" -> {
            imgRes = R.drawable.heavyrain
        }
        "15", "16", "17", "18", "22", "33", "34", "35", "36", "41" -> {
            imgRes = R.drawable.storm
        }
        "19", "21" -> {
            imgRes = R.drawable.suncloudrain
        }
        "23", "42" -> {
            imgRes = R.drawable.snow
        }
        else -> {
            imgRes = R.drawable.snowrain
        }
    }
    imgView.setImageResource(imgRes)
}

@BindingAdapter("nowWeatherImg")
fun setNowWeatherImg(imgView: ImageView, weather: String?) {
    val imgRes: Int
    if (weather != null) {
        if (weather.contains("晴")) {
            imgRes = if (weather.contains("雨")) {
                R.drawable.suncloudrain
            } else if(weather.contains("靄")) {
                R.drawable.suncloud
            } else {
                R.drawable.sun
            }
        } else if (weather.contains("多雲")) {
            imgRes = if (weather.contains("雨")) {
                R.drawable.rainy
            } else {
                R.drawable.clouds
            }
        } else if (weather.contains("陰")) {
            imgRes = if (weather.contains("雨")) {
                R.drawable.heavyrain
            } else {
                R.drawable.cloudy
            }
        } else if (weather.contains("雷") || weather.contains("閃電")) {
            imgRes = if (weather.contains("雨")) {
                R.drawable.storm
            } else {
                R.drawable.thunder
            }
        } else if (weather.contains("雪")) {
            imgRes = R.drawable.snow
        } else {
            imgRes = R.drawable.clouds
        }
    } else {
        imgRes = R.drawable.clouds
    }
    imgView.setImageResource(imgRes)
}

//設定降雨機率文字
@BindingAdapter("popText")
fun setPopText(textView: TextView, weatherPop: String?) {
    weatherPop.let {
        textView.text = "降雨機率: $weatherPop%"
    }
}

//設定 location 文字
@BindingAdapter("locationText")
fun setLocationText(textView: TextView, location: Array<String>?) {
    location.let {
        textView.text = "您的位置: ${location?.get(0)} ${location?.get(1)}"
    }
}

/* 設定 forecast weather api status 以顯示 loading 和 error status 的 icon */
@BindingAdapter("forecastWeatherApiStatus")
fun bindForecastStatus(imageView: ImageView, status: ForecastWeatherApiStatus?) {
    when (status) {
        ForecastWeatherApiStatus.LOADING -> {
            // 將 status ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 loading animation
            imageView.setImageResource(R.drawable.loading_animation)
        }
        ForecastWeatherApiStatus.ERROR -> {
            // 將 state ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 connection error 圖示
            imageView.setImageResource(R.drawable.ic_connection_error)
        }
        ForecastWeatherApiStatus.DONE -> {
            // 將 state ImageView 設定為不可顯示
            imageView.visibility = View.GONE
        }
        else -> {}
    }
}

/* 設定 forecast weather api status 以顯示 loading 和 error status 的 icon */
@BindingAdapter("nowWeatherApiStatus")
fun bindNowStatus(imageView: ImageView, status: NowWeatherApiStatus?) {
    when (status) {
        NowWeatherApiStatus.LOADING -> {
            // 將 status ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 loading animation
            imageView.setImageResource(R.drawable.loading_animation)
        }
        NowWeatherApiStatus.ERROR -> {
            // 將 state ImageView 設為可顯示
            imageView.visibility = View.VISIBLE
            // 設定 connection error 圖示
            imageView.setImageResource(R.drawable.ic_connection_error)
        }
        NowWeatherApiStatus.DONE -> {
            // 將 state ImageView 設定為不可顯示
            imageView.visibility = View.GONE
        }
        else -> {}
    }
}