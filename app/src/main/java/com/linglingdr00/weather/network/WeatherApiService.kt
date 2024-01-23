package com.linglingdr00.weather.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
    suspend fun getAllData(
        @Query("Authorization") key: String,
        @Query("locationName") city: String? = null
    ): WeatherData
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        // 初始化 retrofitService
        retrofit.create(WeatherApiService::class.java)
    }
}