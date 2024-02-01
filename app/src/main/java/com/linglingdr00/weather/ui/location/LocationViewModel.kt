package com.linglingdr00.weather.ui.location

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.now.NowItem

class LocationViewModel : ViewModel() {

    private val TAG = "LocationViewModel"

    private val _location = MutableLiveData<Array<String>>()
    val location: LiveData<Array<String>> = _location

    // 提供給 recycler view 顯示的 ForecastItem
    private val _forecastItem = MutableLiveData<List<ForecastItem>>()
    val forecastItem: LiveData<List<ForecastItem>> = _forecastItem

    // 提供給 recycler view 顯示的 NowItem
    private val _nowItem = MutableLiveData<List<NowItem>>()
    val nowItem: LiveData<List<NowItem>> = _nowItem

    // NowWeatherApi 狀態的 enum class
    enum class LocationWeatherApiStatus { LOADING, ERROR, DONE }

    // 儲存 NowWeatherApi 的狀態
    private val _status = MutableLiveData<LocationWeatherApiStatus>()
    val status: LiveData<LocationWeatherApiStatus> = _status

    fun receiveLocation(city: String, town: String) {
        Log.d(TAG, "city: $city, town: $town")
        _location.value = arrayOf(city, town)
        Log.d(TAG, "location: ${_location.value}")
    }

    fun receiveForecastItem(forecastItem: MutableList<ForecastItem>) {
        _forecastItem.value = forecastItem
        Log.d(TAG, "forecastItem: ${_forecastItem.value}")
    }

    fun receiveNowItem(nowItem: MutableList<NowItem>) {
        _nowItem.value = nowItem
        Log.d(TAG, "nowItem: ${_nowItem.value}")
    }

}