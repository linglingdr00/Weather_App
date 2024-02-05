package com.linglingdr00.weather.ui.location

import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linglingdr00.weather.R
import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.now.NowItem

class LocationViewModel : ViewModel() {

    private val TAG = "LocationViewModel"

    // location LiveData
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    // address LiveData
    private val _address = MutableLiveData<List<Address>?>()
    val address: LiveData<List<Address>?> = _address

    // now item
    private val _nowItem = MutableLiveData<NowItem>()
    val nowItem: LiveData<NowItem> = _nowItem

    // forecast item
    private val _forecastItem = MutableLiveData<ForecastItem>()
    val forecastItem: LiveData<ForecastItem> = _forecastItem

    // location item
    private val _locationItemList = MutableLiveData<List<LocationItem>>()
    val locationItemList: LiveData<List<LocationItem>> = _locationItemList

    // now item status
    private val _nowItemStatus = MutableLiveData<Boolean>()
    val nowItemStatus: MutableLiveData<Boolean> = _nowItemStatus

    // forecast item status
    private val _forecastItemStatus = MutableLiveData<Boolean>()
    val forecastItemStatus: MutableLiveData<Boolean> = _forecastItemStatus

    // permission status
    private val _permissionStatus = MutableLiveData<Boolean>()
    val permissionStatus: LiveData<Boolean> = _permissionStatus

    // location status
    enum class LocationStatus { LOCATION_LOADING, LOCATION_DONE, LOCATION_ERROR, DATA_LOADING, DATA_DONE, DATA_ERROR }
    private val _locationStatus = MutableLiveData<LocationStatus>()
    val locationStatus: LiveData<LocationStatus> = _locationStatus

    // error message
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private var data: List<LocationItem>? = null

    init {
        _nowItemStatus.value = false
        _forecastItemStatus.value = false
    }


    // 設定 permission status
    fun setPermissionStatus(status: Boolean) {
        _permissionStatus.value = status
    }

    // 設定 location status
    fun setLocationState(status: LocationStatus) {
        _locationStatus.value = status
    }

    // 設定 error message
    fun setErrorMessage(message: Int) {
        _errorMessage.value = message
    }

    fun receiveLocation(location: Location?, address: List<Address>?) {

        if (location != null && address != null) {
            // 將 location 存入 LiveData
            _location.value = location
            Log.d(TAG, "location: $location")

            // 將 address 存入 LiveData
            _address.value = address
            Log.d(TAG, "address: $address")

            val countryCode = address[0].countryCode

            // 確認目前所在國家
            checkCountry(countryCode)
        } else {
            // 如果接收到的 location 或 address 為 null
            _locationStatus.value = LocationStatus.LOCATION_ERROR
            _errorMessage.value = R.string.error_message_3_location
        }

    }

    fun setForecastItemStatus(status: Boolean) {
        _forecastItemStatus.value = status
    }

    fun setNowItemStatus(status: Boolean) {
        _nowItemStatus.value = status
    }

    fun tranToLocationItem(nowItem: NowItem, forecastItem: ForecastItem) {
        _locationItemList.value = listOf(
            LocationItem(nowItem),
            LocationItem(forecastItem)
        )
    }

    private fun checkCountry(countryCode: String) {
        // 如果所在國家為台灣
        if (countryCode == "TW") {
            // 將 location 狀態設為 done
            _locationStatus.value = LocationStatus.LOCATION_DONE
        } else {
            // 如果所在國家非台灣，將 location 狀態設為 error
            _locationStatus.value = LocationStatus.LOCATION_ERROR
            _errorMessage.value = R.string.error_message_2_country
        }
    }

}