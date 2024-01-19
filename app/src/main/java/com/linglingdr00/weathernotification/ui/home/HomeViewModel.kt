package com.linglingdr00.weathernotification.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linglingdr00.weathernotification.network.WeatherApi
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val API_KEY = "CWA-ED867C96-5D9B-44D4-A82F-619948365F33"
    private val LOCATION_NAME = "嘉義縣"
    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    init {
        getWeatherData()
    }

    private fun getWeatherData() {

        viewModelScope.launch {
            try {
                val data = WeatherApi.retrofitService.getAllData(key=API_KEY,city=LOCATION_NAME)
                _text.value = data.toString()
                //_text.value = data.records.location.get(0).locationName.toString()
            } catch (e: Exception) {
                _text.value = "Failure: ${e.message}"
            }
        }
    }
}