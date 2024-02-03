package com.linglingdr00.weather.ui.forecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linglingdr00.weather.network.ForecastData
import com.linglingdr00.weather.network.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForecastViewModel() : ViewModel() {

    private val TAG = "ForecastViewModel"
    // 授權碼
    private val API_KEY = "CWA-ED867C96-5D9B-44D4-A82F-619948365F33"

    // 提供給 recycler view 顯示的 ForecastItem list
    private val _forecastItemList = MutableLiveData<List<ForecastItem>>()
    val forecastItemList: LiveData<List<ForecastItem>> = _forecastItemList

    // 儲存取得資料的 ArrayList
    private var dataArrayList: ArrayList<MutableMap<String, String>> = arrayListOf()
    // ForecastWeatherApi 狀態的 enum class
    enum class ForecastWeatherApiStatus { LOADING, ERROR, DONE }

    // 儲存 ForecastWeatherApi 的狀態
    private val _status = MutableLiveData<ForecastWeatherApiStatus>()
    val status: LiveData<ForecastWeatherApiStatus> = _status

    init {
        //getForecastData()
    }

    fun getForecastData(city: String? = null) {

        //設定 ForecastWeatherApi 狀態為 LOADING
        _status.value = ForecastWeatherApiStatus.LOADING
        Log.d(TAG, "status: ${status.value}")

        // 呼叫 ForecastWeatherApi 取得所有資料
        val call = WeatherApi.retrofitService.getForecastData(key = API_KEY)

        call.enqueue(object: Callback<ForecastData> {
            override fun onResponse(
                call: Call<ForecastData>,
                response: Response<ForecastData>
            ) {
                // 如果 response 成功
                if (response.isSuccessful) {
                    // 將得到的資料儲存在 forecastData
                    val forecastData = response.body()
                    // 處理資料，將 forecastData 轉成 ArrayList
                    val newArrayList = handleData(forecastData)
                    // 將 dataArrayList 設為處理好的資料
                    dataArrayList = newArrayList
                    // 設定 ForecastWeatherApi 狀態為 DONE
                    _status.value = ForecastWeatherApiStatus.DONE
                    Log.d(TAG, "status: ${_status.value}")
                } else {
                    Log.d(TAG, "Error Code: ${response.code()}")
                    // 設定 ForecastWeatherApi 狀態為 ERROR
                    _status.value = ForecastWeatherApiStatus.ERROR
                    Log.d(TAG, "status: ${_status.value}")
                    // 設 forecastItemList 為空 list
                    _forecastItemList.value = listOf()
                }
            }

            override fun onFailure(call: Call<ForecastData>, t: Throwable) {
                // 設定 WeatherApi 狀態為 ERROR
                _status.value = ForecastWeatherApiStatus.ERROR
                Log.d(TAG, "status: ${_status.value}")
                // 設 forecastItemList 為空 list
                _forecastItemList.value = listOf()
                Log.d(TAG, "Failure: ${t.message}")
            }
        })
    }

    private fun datetimeFormat(timeString: String): String {
        // 將 string 轉成 LocalDateTime
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dt = LocalDateTime.parse(timeString, formatter1)
        //Log.d(TAG,"dateTime: $dt")

        // 取得今天日期
        val now = LocalDate.now()

        val date = when(now.equals(dt.toLocalDate())) {
            true -> "今天"
            false -> "明天"
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

    // 將所需資料存在 map 中，再用 ArrayList 包起來
    private fun handleData(forecastData: ForecastData?)
    : ArrayList<MutableMap<String, String>> {

        // 新增一個 mapArrayList 存放所需資料
        val mapArrayList = ArrayList<MutableMap<String, String>>()

        forecastData?.records?.location?.forEach { location ->
            // 新增一個 map 存放資料(key, value)
            val hashMap: MutableMap<String, String> = mutableMapOf()

            val locationName = location.locationName
            //Log.d(TAG, "locationName: $locationName")
            hashMap["location"] = locationName

            location.weatherElement.forEach { weatherElement ->
                val elementName = weatherElement.elementName
                //Log.d(TAG, "elementName: $elementName")

                weatherElement.time.forEachIndexed { index, time ->
                    val timeIndex = index+1
                    val startTime = datetimeFormat(time.startTime)
                    val endTime = datetimeFormat(time.endTime)
                    val timeString = "$startTime - $endTime"
                    hashMap["time$timeIndex"] = timeString

                    val parameter = time.parameter.parameterName
                    val name = elementName
                    hashMap["$name$timeIndex"] = parameter

                    if (name=="Wx") {
                        // 取得天氣代碼
                        val weatherCode = time.parameter.parameterValue
                        hashMap["weatherCode$timeIndex"] = weatherCode
                    }
                }
            }
            // 將 map 加入 ArrayList 中
            mapArrayList.add(hashMap)
            //Log.d("$mapArrayList")
        }

        // 轉換溫度格式
        val newArrayList = temperatureFormat(mapArrayList)

        return newArrayList
    }

    private fun temperatureFormat(arrayList: ArrayList<MutableMap<String, String>>)
    : ArrayList<MutableMap<String, String>> {
        // temperature format
        arrayList.forEachIndexed { index, mutableMap ->

            for (i in 1..3) {
                // 把 MinT(最低溫) 和 MaxT(最高溫) 結合成一個 string
                val minT = mutableMap["MinT$i"]
                //Log.d(TAG, "minT: $minT")
                val maxT = mutableMap["MaxT$i"]
                //Log.d(TAG, "maxT: $maxT")
                val temperature = "$minT°C - $maxT°C"
                //Log.d(TAG, "temperature$i: $temperature")
                mutableMap["temperature$i"] = temperature
                // 移除 MinT 和 MaxT
                mutableMap.remove("MinT$i")
                mutableMap.remove("MaxT$i")
            }
            //Log.d(TAG, "$index: ${arrayList[index]}")
        }
        return arrayList
    }

    // 轉換成 ForecastItem
    private fun transToForecastItem(list: List<MutableMap<String, String>>): MutableList<ForecastItem> {
        val tempList = mutableListOf<ForecastItem>()
        list.forEach { map ->
            val item = ForecastItem(
                map["location"].toString(),
                map["time1"].toString(),
                map["weatherCode1"].toString(),
                map["Wx1"].toString(),
                map["PoP1"].toString(),
                map["temperature1"].toString(),
                map["time2"].toString(),
                map["weatherCode2"].toString(),
                map["Wx2"].toString(),
                map["PoP2"].toString(),
                map["temperature2"].toString(),
                map["time3"].toString(),
                map["weatherCode3"].toString(),
                map["Wx3"].toString(),
                map["PoP3"].toString(),
                map["temperature3"].toString()
            )
            tempList += item
            Log.d(TAG, "tempList: $tempList")
        }
        //_forecastItemList.value = tempList
        //Log.d(TAG, "_forecastItem: ${_forecastItemList.value}")
        return tempList
    }

    // 根據選擇的區域設定不同資料
    fun setAreaData(cityList: MutableList<String>) {
        // 新增一個空 List
        val dataList: MutableList<MutableMap<String, String>> = mutableListOf()
        // 跑需要加入空 list 的每個縣市
        for (city in cityList) {
            val tempList = dataArrayList.filter {
                it["location"].equals(city)
            }
            //Log.d(TAG, "data: $dataList")
            dataList += tempList
        }
        //Log.d(TAG, "dataList: $dataList")

        // 將新的 ArrayList 資料轉成 ForecastItem
        val itemList = transToForecastItem(dataList)
        _forecastItemList.value = itemList
        Log.d(TAG, "_forecastItem: ${_forecastItemList.value}")
    }

    fun getMyCityData(city: String): MutableList<ForecastItem> {

        val myCity = when(city) {
            "台北市" -> "臺北市"
            else -> city
        }

        val tempList = dataArrayList.filter {
            it["location"].equals(myCity)
        }
        //Log.d(TAG, "data: $tempList")
        // 將資料轉成 ForecastItem
        return transToForecastItem(tempList)
    }
}
