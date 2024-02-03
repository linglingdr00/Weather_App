package com.linglingdr00.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.linglingdr00.weather.databinding.ActivityMainBinding
import com.linglingdr00.weather.ui.forecast.ForecastViewModel
import com.linglingdr00.weather.ui.location.LocationViewModel
import com.linglingdr00.weather.ui.now.NowViewModel
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 載入 BottomNavigation
        loadBottomNavigation()

        // 預先載入資料
        preLoadData()
    }

    private fun loadBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        setSupportActionBar(binding.toolbar)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        // 檢查有沒有 location 權限
        checkLocationPermission()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun checkLocationPermission() {
        when {
            // 當已取得權限時
            ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "checkLocationPermission(): 已取得位置權限")
                permissionApproved()
            }
            // 當未取得權限，且拒絕次數小於2次時
            ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Log.d(TAG, "checkLocationPermission(): 未取得位置權限")

                // 告知需要權限的原因
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_require_title)
                    .setMessage(R.string.permission_request)
                    .setPositiveButton("OK") { _, _ ->
                        // 再次請求權限
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                    .setNeutralButton("NO") { _, _ -> permissionDenied() }
                    .show()
            }
            // 當第一次詢問時
            else -> {
                Log.d(TAG, "checkLocationPermission(): 直接請求位置權限")
                locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        // 使用者同意給予權限
        if (isGranted) {
            permissionApproved()
        }
        // 使用者不同意給予權限
        else {
            // 拒絕次數超過2次時
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                //permissionDenied()

                // 引導使用者到設定頁面的 Dialog
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_require_title)
                    .setMessage(R.string.permission_require)
                    .setPositiveButton("Ok") { _, _ ->
                        // 開啟此 app 的設定頁面
                        openPermissionSettings()
                    }
                    .setNeutralButton("No") { _, _ ->
                        // 顯示沒有權限的訊息
                        permissionDenied()
                    }
                    .show()
            }
            // 拒絕次數小於2次時
            else {
                checkLocationPermission()
            }
        }
    }

    // 開啟此 app 的設定頁面
    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun permissionApproved() {
        // 設定 permission status，並傳給 view model
        locationViewModel.setPermissionStatus(true)
        Snackbar.make(binding.container, R.string.permission_approved, Snackbar.LENGTH_LONG).show()
    }

    private fun permissionDenied() {
        // 設定 permission status，並傳給 view model
        locationViewModel.setPermissionStatus(false)
        Snackbar.make(binding.container, R.string.permission_denied, Snackbar.LENGTH_LONG).show()
    }

    private fun preLoadData() {
        // 取得 ForecastViewModel
        val forecastViewModel: ForecastViewModel by viewModels()
        // 預先載入天氣預報資料
        forecastViewModel.viewModelScope.launch {
            try {
                // 載入天氣預報資料
                forecastViewModel.getForecastData()
                Log.d(TAG, "預先載入天氣預報資料成功")
            } catch (e: Exception) {
                Log.d(TAG, "預先載入天氣預報資料失敗")
            }
        }

        // 取得 NowViewModel
        val nowViewModel: NowViewModel by viewModels()
        // 預先載入現在天氣資料
        nowViewModel.viewModelScope.launch {
            try {
                // 載入現在天氣資料
                nowViewModel.getNowData()
                Log.d(TAG, "預先載入現在天氣資料成功")
            } catch (e: Exception) {
                Log.d(TAG, "預先載入現在天氣資料失敗")
            }
        }
    }

}
