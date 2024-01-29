package com.linglingdr00.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.linglingdr00.weather.databinding.ActivityMainBinding
import com.linglingdr00.weather.ui.forecast.ForecastViewModel
import com.linglingdr00.weather.ui.now.NowViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        setSupportActionBar(binding.toolbar)
        navView.setupWithNavController(navController)

        Log.d(TAG,"navView selectedItemId: ${navView.selectedItemId}")

        // 取得 ForecastViewModel
        val forecastViewModel: ForecastViewModel by viewModels()
        // 預先載入天氣預報資料
        forecastViewModel.viewModelScope.launch {
            try {
                //載入天氣預報資料
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
                //載入現在天氣資料
                nowViewModel.getNowData()
                Log.d(TAG, "預先載入現在天氣資料成功")
            } catch (e: Exception) {
                Log.d(TAG, "預先載入現在天氣資料失敗")
            }
        }

    }
}
