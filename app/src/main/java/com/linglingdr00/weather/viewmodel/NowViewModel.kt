package com.linglingdr00.weather.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linglingdr00.weather.model.data.NowData
import com.linglingdr00.weather.model.data.NowItem
import com.linglingdr00.weather.model.network.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NowViewModel : ViewModel() {

    private val TAG = "NowViewModel"
    // 授權碼
    private val API_KEY = "CWA-ED867C96-5D9B-44D4-A82F-619948365F33"

    // 提供給 recycler view 顯示的 NowItem list
    private val _nowItemList = MutableLiveData<List<NowItem>>()
    val nowItemList: LiveData<List<NowItem>> = _nowItemList

    // 儲存取得資料的 ArrayList
    private var dataArrayList: ArrayList<MutableMap<String, String>> = arrayListOf()

    // NowWeatherApi 狀態的 enum class
    enum class NowWeatherApiStatus { LOADING, ERROR, DONE }

    // 儲存 NowWeatherApi 的狀態
    private val _status = MutableLiveData<NowWeatherApiStatus>()
    val status: LiveData<NowWeatherApiStatus> = _status

    init {
        //getNowData()
    }

    fun getNowData() {

        //設定 NowWeatherApi 狀態為 LOADING
        _status.value = NowWeatherApiStatus.LOADING
        Log.d(TAG, "status: ${status.value}")

        // 呼叫 NowWeatherApi 取得所有資料
        val call = WeatherApi.retrofitService.getNowData(key = API_KEY)

        call.enqueue(object: Callback<NowData>{
            override fun onResponse(call: Call<NowData>, response: Response<NowData>) {
                // 如果 response 成功
                if (response.isSuccessful) {
                    Log.d(TAG, "success")
                    // 將得到的資料儲存在 nowData
                    val nowData = response.body()
                    // 處理資料，將 forecastData 轉成 ArrayList
                    val newArrayList = handleData(nowData)
                    // 將 dataArrayList 設為處理好的資料
                    dataArrayList = newArrayList
                    // 設定 NowWeatherApi 狀態為 DONE
                    _status.value = NowWeatherApiStatus.DONE
                    Log.d(TAG, "status: ${_status.value}")

                } else {
                    Log.d(TAG, "error: ${response.code()}")
                    //設定 NowWeatherApi 狀態為 ERROR
                    _status.value = NowWeatherApiStatus.ERROR
                    Log.d(TAG, "status: ${_status.value}")
                    //設 NowItemList 為空 list
                    _nowItemList.value = listOf()
                }
            }

            override fun onFailure(call: Call<NowData>, t: Throwable) {
                //設定 NowWeatherApi 狀態為 ERROR
                _status.value = NowWeatherApiStatus.ERROR
                Log.d(TAG, "status: ${_status.value}")
                //設 NowItemList 為空 list
                _nowItemList.value = listOf()
                Log.d(TAG, "Failure: ${t.message}")
            }

        })
    }

    // 將所需資料存在 map 中，再用 ArrayList 包起來
    private fun handleData(nowData: NowData?)
            : ArrayList<MutableMap<String, String>> {

        // 新增一個 mapArrayList 存放所需資料
        val mapArrayList = ArrayList<MutableMap<String, String>>()

        nowData?.records?.station?.forEach { station ->
            // 新增一個 map 存放資料(key, value)
            val hashMap: MutableMap<String, String> = mutableMapOf()

            val stationName = "測站: ${station.stationName}"
            hashMap["stationName"] = stationName

            val countyName = station.geoInfo.countyName
            hashMap["countyName"] = countyName

            val townName = station.geoInfo.townName
            hashMap["townName"] = townName

            val stationLatitude = station.geoInfo.coordinates[0].stationLatitude
            hashMap["stationLatitude"] = stationLatitude.toString()

            val stationLongitude = station.geoInfo.coordinates[0].stationLongitude
            hashMap["stationLongitude"] = stationLongitude.toString()

            val weather = when (val tempWeather = station.weatherElement.weather) {
                "-99" -> "資料異常"
                else -> tempWeather
            }
            hashMap["weather"] = weather

            val uv = when (val tempUV = station.weatherElement.uvIndex.toString()) {
                "X" -> "紫外線指數: 儀器故障"
                "-99.0" -> "紫外線指數: 0.0 UVI"
                else -> "紫外線指數: $tempUV UVI"
            }
            hashMap["uv"] = uv

            val rain = when (val tempRain = station.weatherElement.now.precipitation.toString()) {
                "X" -> "當日降水量: 儀器故障"
                "-990.0" -> "當日降水量: 資料異常"
                "-99.0" -> "當日降水量: 資料異常"
                "-98.0" -> "當日降水量: 連續6小時無降水"
                else -> "當日降水量: ${tempRain}毫米"
            }
            hashMap["rain"] = rain

            val temperature = when (val tempT = station.weatherElement.airTemperature.toString()) {
                "X" -> "儀器故障"
                "-99.0" -> "資料異常"
                else -> "${tempT}°C"
            }
            hashMap["temperature"] = temperature

            val lowTemperature = when (val tempLowT = station.weatherElement.dailyExtreme.dailyLow.temperatureInfo.airTemperature.toString()) {
                "X" -> "當日最低溫: 儀器故障"
                "-99.0" -> "當日最低溫: 資料異常"
                else -> "當日最低溫: ${tempLowT}°C"
            }
            hashMap["lowTemperature"] = lowTemperature

            val highTemperature = when (val tempHighT = station.weatherElement.dailyExtreme.dailyHigh.temperatureInfo.airTemperature.toString()) {
                "X" -> "當日最高溫: 儀器故障"
                "-99.0" -> "當日最高溫: 資料異常"
                else -> "當日最高溫: ${tempHighT}°C"
            }
            hashMap["highTemperature"] = highTemperature

            // 將 map 加入 ArrayList 中
            mapArrayList.add(hashMap)
            //Log.d(TAG,"$mapArrayList")
        }

        //transToNowItem(mapArrayList)

        return mapArrayList
    }

    // 轉換成 NowItem
    private fun transToNowItem(list: List<MutableMap<String, String>>): MutableList<NowItem> {
        val tempList = mutableListOf<NowItem>()
        list.forEach { map ->
            val item = NowItem(
                map["stationName"].toString(),
                map["townName"].toString(),
                map["weather"].toString(),
                map["uv"].toString(),
                map["rain"].toString(),
                map["temperature"].toString(),
                map["lowTemperature"].toString(),
                map["highTemperature"].toString()
            )
            tempList += item
            Log.d(TAG, "tempList: $tempList")
        }
        return tempList
    }

    // 根據選擇的區域設定不同資料
    fun setCityData(city: String) {

        val dataList = dataArrayList.filter {
            it["countyName"].equals(city)
        }
        //Log.d(TAG, "data: $dataList")

        val tempList = transToNowItem(dataList)
        _nowItemList.value = tempList
        Log.d(TAG, "_nowItem: ${_nowItemList.value}")
    }

    fun getMyTownData(location: Location): MutableList<NowItem> {

        //新增一個空 List
        val myList: MutableList<MutableMap<String, String>> = mutableListOf()
        /*val dataList = dataArrayList.filter {
            it["townName"].equals(town)
        }.toMutableList()*/


        val lat = location.latitude
        val lon = location.longitude
        val myTown = getTownByLocation(lat, lon)
        Log.d(TAG, "lat: $lat, lon: $lon")

        Log.d(TAG, "dataList: $myTown")
        myList += myTown

        return transToNowItem(myList)
    }


    private fun getTownByLocation(latitude: Double, longitude: Double): MutableMap<String, String> {

        Log.d(TAG, "latitude: $latitude, longitude: $longitude")
        val tempList = dataArrayList

        tempList.sortBy{
            Math.abs(it["stationLatitude"]?.toDouble()!! - latitude) +
                    Math.abs(it["stationLongitude"]?.toDouble()!! - longitude)
        }

        //Log.d(TAG, "result: $result")
        //val myTown = tempList[0]
        Log.d(TAG, "tempList: $tempList")
        return tempList[0]
    }
}