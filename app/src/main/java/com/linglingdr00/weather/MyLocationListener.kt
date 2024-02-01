package com.linglingdr00.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Locale


class MyLocationListener(private val context: Context, private val callback: (String, String) -> Unit) {

    private val TAG = "MyLocationListener"
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener = LocationListener { location ->
        //經度
        val latitude = location.latitude
        //緯度
        val longitude = location.longitude
        Log.d(TAG, "location latitude: ${location.latitude}, longitude: ${location.longitude}")
        //將經緯度轉成地址
        tranToAddress(latitude, longitude)
    }

    fun startGetLocation() {
        //取得 LocationManager
        locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        //使用 requestLocationUpdates 開始更新經緯度
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L,0F,locationListener)
    }

    private fun stopGetLocation() {
        //移除 locationListener
        locationManager?.removeUpdates(locationListener)
    }

    //將經緯度轉成地址
    private fun tranToAddress(latitude: Double, longitude: Double) {
        val addressList: List<Address>?
        val geocoder = Geocoder(context, Locale.getDefault())

        //得到地址資訊
        addressList = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addressList!![0].getAddressLine(0)
        val city = addressList[0].adminArea
        val town = addressList[0].subAdminArea
        Log.d(TAG, "address: $address")
        Log.d(TAG, "city: $city")
        Log.d(TAG, "town: $town")

        //傳回 city 和 town
        callback.invoke(city, town)
        //停止更新 location
        stopGetLocation()
    }
}
