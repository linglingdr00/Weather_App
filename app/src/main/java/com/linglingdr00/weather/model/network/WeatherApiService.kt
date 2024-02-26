package com.linglingdr00.weather.model.network

import com.linglingdr00.weather.model.data.ForecastData
import com.linglingdr00.weather.model.data.NowData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 新增 base URL
private const val BASE_URL = "https://opendata.cwa.gov.tw/api/"

// 新增 moshi object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// 新增 retrofit object
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    @GET("v1/rest/datastore/F-C0032-001")
    fun getForecastData(
        @Query("Authorization") key: String,
        @Query("locationName") city: String? = null
    ): Call<ForecastData>

    @GET("v1/rest/datastore/O-A0003-001")
    fun getNowData(
        @Query("Authorization") key: String,
        @Query("StationName") station: String? = null
    ): Call<NowData>

}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        // 初始化 retrofitService
        retrofit.create(WeatherApiService::class.java)
    }
}