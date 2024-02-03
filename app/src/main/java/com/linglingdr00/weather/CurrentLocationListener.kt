package com.linglingdr00.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.Locale
import java.util.concurrent.TimeUnit


class CurrentLocationListener(private val context: Context, private val callback: (Location, List<Address>) -> Unit) {

    private val TAG = "MyLocationListener"

    private var locationManager: LocationManager? = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    private val networkProvider = LocationManager.NETWORK_PROVIDER
    private val gpsProvider = LocationManager.GPS_PROVIDER

    private var locationListener: LocationListener = object: LocationListener {

        override fun onLocationChanged(location: Location) {
            // 得到 location 後
            afterGetLocationData(location)
        }

        override fun onProviderDisabled(provider: String) {
        }

        override fun onProviderEnabled(provider: String) {
        }
    }

    private fun afterGetLocationData(location: Location) {
        Log.d(TAG, "latitude: ${location.latitude}, longitude: ${location.longitude}")
        // 停止更新位置
        //stopGetLocation()
        stopToLocationUpdates()
        // 將經緯度轉成地址
        tranToAddress(location)
    }

    fun startGetLocation() {
        // 使用 requestLocationUpdates 開始更新經緯度
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        when (val location = locationManager?.getLastKnownLocation(networkProvider)) {
            null -> {
                gpsProvider.let { locationManager!!.requestLocationUpdates(it,0L,0F,locationListener) }
            } else -> {
                afterGetLocationData(location)
            }
        }

    }

    private fun stopGetLocation() {
        // 移除 locationListener
        locationManager?.removeUpdates(locationListener)
    }

    // 將經緯度轉成地址
    private fun tranToAddress(location: Location) {
        val addressList: List<Address>?
        val geocoder = Geocoder(context, Locale.getDefault())

        val latitude = location.latitude
        val longitude = location.longitude

        // 得到地址資訊
        addressList = geocoder.getFromLocation(latitude, longitude, 1)

        // 傳回 location 和 address
        if (addressList != null) {
            callback.invoke(location, addressList)
        }
    }

    // gms
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    fun createLocationRequest() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(0)
            fastestInterval = TimeUnit.SECONDS.toMillis(0)
            maxWaitTime = TimeUnit.MINUTES.toMillis(5)
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation
                Log.d(TAG, "currentLocation: $currentLocation")
                afterGetLocationData(currentLocation!!)
            }
        }
    }

    fun startToLocationUpdates() {
        Log.d(TAG, "startToLocationUpdates()")

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun stopToLocationUpdates() {
        Log.d(TAG, "startToLocationUpdates()")

        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

}
