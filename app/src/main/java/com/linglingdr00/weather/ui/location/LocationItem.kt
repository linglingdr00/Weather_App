package com.linglingdr00.weather.ui.location

import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.now.NowItem

data class LocationItem(
    val nowItem: NowItem,
    val forecastItem: ForecastItem
)