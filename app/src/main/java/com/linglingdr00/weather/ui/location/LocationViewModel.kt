package com.linglingdr00.weather.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel


class LocationViewModel : ViewModel() {

    private val TAG = "LocationViewModel"

    fun receiveLocation(longitude: Double?, latitude:Double?) {
        Log.d(TAG, "經度: $longitude, 緯度: $latitude")
    }

}