package com.linglingdr00.weather.ui.forecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linglingdr00.weather.network.WeatherApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForecastViewModel() : ViewModel() {

    private val TAG = "ForecastViewModel"
    private val API_KEY = "CWA-ED867C96-5D9B-44D4-A82F-619948365F33"
    private val LOCATION_NAME = "嘉義縣"

    //private val _text = MutableLiveData<String>()
    //val text: LiveData<String> = _text

    private val _forecastItemList = MutableLiveData<List<ForecastItem>>()
    val forecastItemList: LiveData<List<ForecastItem>> = _forecastItemList

    init {
        getWeatherData()
    }

    private fun getWeatherData() {
        //_text.value = "天氣預報"

        viewModelScope.launch {
            try {
                //呼叫 WeatherApi 取得所有資料
                val allData = WeatherApi.retrofitService.getAllData(key = API_KEY)
                //新增一個 mapArrayList 存放所需資料
                val mapArrayList = ArrayList<MutableMap<String, String>>()

                allData.records.location.forEachIndexed { index, location ->
                    //新增一個 map 存放資料(key, value)
                    val hashMap: MutableMap<String, String> = mutableMapOf()

                    val locationName = location.locationName
                    //Log.d(TAG, "index: $index, locationName: $locationName")
                    hashMap.put("location", locationName)

                    location.weatherElement.forEachIndexed { index, weatherElement ->
                        val elementName = weatherElement.elementName
                        //Log.d(TAG, "index: $index, elementName: $elementName")

                        weatherElement.time.forEachIndexed { index, time ->
                            val timeIndex = index+1
                            val startTime = datetimeFormat(time.startTime)
                            val endTime = datetimeFormat(time.endTime)
                            val timeString = "$startTime - $endTime"
                            hashMap.put("time$timeIndex", timeString)

                            val parameter = time.parameter.parameterName
                            val name = elementName
                            hashMap.put("$name$timeIndex", parameter)

                            if (name=="Wx") {
                                //取得天氣代碼
                                val weatherCode = time.parameter.parameterValue
                                hashMap.put("weatherCode$timeIndex", weatherCode)
                            }
                        }
                    }
                    mapArrayList.add(hashMap)
                    //Log.d("$index: ", "${mapArrayList[index]}")
                }
                handleData(mapArrayList)
            } catch (e: Exception) {
                //_text.value = "Failure: ${e.message}"
                Log.d(TAG, "Failure: ${e.message}")
            }
        }
    }

    // 處理 data (array list 可使用 key 取得 value)
    private fun handleData(arrayList: ArrayList<MutableMap<String, String>>) {

        // temperature format
        arrayList.forEachIndexed { index, mutableMap ->

            for (i in 1..3) {
                // 把 MinT(最低溫) 和 MaxT(最高溫) 結合成一個 string
                val minT = mutableMap.get("MinT$i")
                //Log.d(TAG, "minT: $minT")
                val maxT = mutableMap.get("MaxT$i")
                //Log.d(TAG, "maxT: $maxT")
                val temperature = "$minT°C - $maxT°C"
                //Log.d(TAG, "temperature$i: $temperature")
                mutableMap.put("temperature$i", temperature)
                // 移除 MinT 和 MaxT
                mutableMap.remove("MinT$i")
                mutableMap.remove("MaxT$i")
            }
            Log.d("$index: ", "${arrayList[index]}")
        }
        transToForecastItem(arrayList)
    }

    // 轉換成 transToForecastItem
    private fun transToForecastItem(arrayList: ArrayList<MutableMap<String, String>>) {
        val tempList = mutableListOf<ForecastItem>()
        arrayList.forEachIndexed { index, mutableMap ->
            val item = ForecastItem(
                mutableMap.get("location").toString(),
                mutableMap.get("time1").toString(),
                mutableMap.get("weatherCode1").toString(),
                mutableMap.get("Wx1").toString(),
                mutableMap.get("PoP1").toString(),
                mutableMap.get("temperature1").toString(),
                mutableMap.get("time2").toString(),
                mutableMap.get("weatherCode2").toString(),
                mutableMap.get("Wx2").toString(),
                mutableMap.get("PoP2").toString(),
                mutableMap.get("temperature2").toString(),
                mutableMap.get("time3").toString(),
                mutableMap.get("weatherCode3").toString(),
                mutableMap.get("Wx3").toString(),
                mutableMap.get("PoP3").toString(),
                mutableMap.get("temperature3").toString()
            )
            //tempList.plus(item)
            tempList += item
            Log.d(TAG, "tempList: $tempList")
        }
        _forecastItemList.value = tempList
        Log.d(TAG, "_forecastItem: ${_forecastItemList.value}")
    }

    private fun datetimeFormat(timeString: String): String {
        //將 string 轉成 LocalDateTime
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dt = LocalDateTime.parse(timeString, formatter1)
        //Log.d(TAG,"dateTime: $dt")

        // 取得今天日期
        val now = LocalDate.now()
        val date: String

        if (now.equals(dt.toLocalDate())) {
            date = "今天"
        } else {
            date = "明天"
        }
        //Log.d(TAG, "date: $date")

        // 將 LocalDateTime 格式化成 string
        val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
        val time = dt.format(formatter2)
        //Log.d(TAG, "time: $time")
        val formatTime = "$date $time"
        //Log.d(TAG, "date time: $formatTime")
        return formatTime
    }
}
