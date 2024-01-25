package com.linglingdr00.weather

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.linglingdr00.weather.databinding.ActivityMainBinding
import com.linglingdr00.weather.ui.forecast.ForecastViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_forecast, R.id.navigation_location, R.id.navigation_now
            )
        )*/
        setSupportActionBar(binding.toolbar)
        navView.setupWithNavController(navController)

        //取得 ForecastViewModel
        val forecastViewModel: ForecastViewModel by viewModels()
        //載入天氣預報資料
        forecastViewModel.getWeatherData()
    }
}
