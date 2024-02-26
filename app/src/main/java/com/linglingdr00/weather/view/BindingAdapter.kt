package com.linglingdr00.weather.view

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linglingdr00.weather.R
import com.linglingdr00.weather.view.forecast.ForecastAdapter
import com.linglingdr00.weather.model.data.ForecastItem
import com.linglingdr00.weather.viewmodel.ForecastViewModel.ForecastWeatherApiStatus
import com.linglingdr00.weather.view.location.LocationAdapter
import com.linglingdr00.weather.model.data.LocationItem
import com.linglingdr00.weather.viewmodel.LocationViewModel.LocationStatus
import com.linglingdr00.weather.view.now.NowAdapter
import com.linglingdr00.weather.model.data.NowItem
import com.linglingdr00.weather.viewmodel.NowViewModel.NowWeatherApiStatus

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

//初始化 LocationAdapter
@BindingAdapter("locationData")
fun bindLocationRecyclerView(recyclerView: RecyclerView, data: List<LocationItem>?) {
    val adapter = recyclerView.adapter as LocationAdapter

    if (data!=null) {
        adapter.setLocationItem(data)
        adapter.notifyDataSetChanged()
    }

}

//根據 weatherCode 設定天氣圖片
@BindingAdapter("forecastWeatherImg")
fun bindForecastWeatherImg(imgView: ImageView, weatherCode: String?) {
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
fun bindNowWeatherImg(imgView: ImageView, weather: String?) {
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
fun bindPopText(textView: TextView, weatherPop: String?) {
    weatherPop.let {
        textView.text = "降雨機率: $weatherPop%"
    }
}

@BindingAdapter("errorMessage")
fun bindErrorMessage(textView: TextView, messageId: Int?) {
    if (messageId != null) {
        textView.setText(messageId)
    }
}

/* 設定 forecast weather api status 以顯示 loading 和 error status 的 icon */
@BindingAdapter("forecastWeatherApiStatus")
fun bindForecastStatus(imageView: ImageView, status: ForecastWeatherApiStatus?) {
    when (status) {
        ForecastWeatherApiStatus.LOADING -> {
            imageView.setImageResource(R.drawable.loading_animation)
            imageView.visibility = View.VISIBLE
        }
        ForecastWeatherApiStatus.ERROR -> {
            imageView.setImageResource(R.drawable.ic_connection_error)
            imageView.visibility = View.VISIBLE
        }
        ForecastWeatherApiStatus.DONE -> {
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
            imageView.setImageResource(R.drawable.loading_animation)
            imageView.visibility = View.VISIBLE
        }
        NowWeatherApiStatus.ERROR -> {
            imageView.setImageResource(R.drawable.ic_connection_error)
            imageView.visibility = View.VISIBLE
        }
        NowWeatherApiStatus.DONE -> {
            imageView.visibility = View.GONE
        }
        else -> {}
    }
}

@BindingAdapter("locationStatus")
fun bindLocationStatus(imageView: ImageView, status: LocationStatus?) {
    when (status) {
        LocationStatus.LOCATION_LOADING, LocationStatus.DATA_LOADING -> {
            imageView.setImageResource(R.drawable.loading_animation)
            imageView.visibility = View.VISIBLE
        }
        LocationStatus.LOCATION_ERROR, LocationStatus.DATA_ERROR -> {
            imageView.setImageResource(R.drawable.ic_location_off)
            imageView.visibility = View.VISIBLE
        }
        LocationStatus.LOCATION_DONE, LocationStatus.DATA_DONE -> {
            imageView.visibility = View.GONE
        }
        else -> {}
    }
}

@BindingAdapter("linerLayuout")
fun bindLinerLayuout(layout: LinearLayout, status: LocationStatus?) {
    when (status) {
        LocationStatus.DATA_DONE -> {
            layout.visibility = View.VISIBLE
        }
        else -> {
            layout.visibility = View.GONE
        }
    }
}