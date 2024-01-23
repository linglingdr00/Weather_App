package com.linglingdr00.weathernotification.model

import androidx.annotation.DrawableRes

data class ForecastItem(
    val locationText: String,
    val timeText1: String,
    @DrawableRes val weatherImg1: Int,
    val weatherText1: String,
    val popText1: String,
    val temperature1: String,
    val timeText2: String,
    @DrawableRes val weatherImg2: Int,
    val weatherText2: String,
    val popText2: String,
    val temperature2: String,
    val timeText3: String,
    @DrawableRes val weatherImg3: Int,
    val weatherText3: String,
    val popText3: String,
    val temperature3: String
)
