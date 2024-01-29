package com.linglingdr00.weather.network

import com.squareup.moshi.Json

data class NowData(
    val records: Records = Records(),
    val result: Result = Result(),
    val success: String = ""
) {
    data class Records(
        @Json(name = "Station")
        val station: List<StationName> = listOf()
    ) {
        data class StationName(
            @Json(name = "GeoInfo")
            val geoInfo: GeoInfo = GeoInfo(),
            @Json(name = "ObsTime")
            val obsTime: ObsTime = ObsTime(),
            @Json(name = "StationId")
            val stationId: String = "", //測站代碼
            @Json(name = "StationName")
            val stationName: String = "", //測站名稱
            @Json(name = "WeatherElement")
            val weatherElement: WeatherElement = WeatherElement()
        ) {
            data class GeoInfo(
                @Json(name = "Coordinates")
                val coordinates: List<Coordinate> = listOf(),
                @Json(name = "CountyCode")
                val countyCode: String = "", //所在縣市代碼
                @Json(name = "CountyName")
                val countyName: String = "", //所在縣市名稱
                @Json(name = "StationAltitude")
                val stationAltitude: Float = 0.0f, //測站高度
                @Json(name = "TownCode")
                val townCode: String = "", //所在鄉鎮市區代碼
                @Json(name = "TownName")
                val townName: String = "" //所在鄉鎮市區名稱
            ) {
                data class Coordinate(
                    @Json(name = "CoordinateFormat")
                    val coordinateFormat: String = "", //座標格式
                    @Json(name = "CoordinateName")
                    val coordinateName: String = "", //座標系名稱
                    @Json(name = "StationLatitude")
                    val stationLatitude: Double = 0.0, //測站緯度
                    @Json(name = "StationLongitude")
                    val stationLongitude: Double = 0.0 //測站經度
                )
            }

            data class ObsTime(
                @Json(name = "DateTime")
                val dateTime: String = "" //觀測時間
            )

            data class WeatherElement(
                @Json(name = "AirPressure")
                val airPressure: Float = 0.0f, //氣壓
                @Json(name = "AirTemperature")
                val airTemperature: Float = 0.0f, //氣溫
                @Json(name = "DailyExtreme")
                val dailyExtreme: DailyExtreme = DailyExtreme(),
                @Json(name = "GustInfo")
                val gustInfo: GustInfo = GustInfo(),
                @Json(name = "Max10MinAverage")
                val max10MinAverage: Max10MinAverage = Max10MinAverage(),
                @Json(name = "Now")
                val now: Now = Now(),
                @Json(name = "RelativeHumidity")
                val relativeHumidity: Int = 0, //相對濕度
                @Json(name = "SunshineDuration")
                val sunshineDuration: Float = 0.0f, //日照時數
                @Json(name = "UVIndex")
                val uvIndex: Float = 0.0f, //紫外線指數
                @Json(name = "VisibilityDescription")
                val visibilityDescription: String = "", //能見度描述
                @Json(name = "Weather")
                val weather: String = "", //天氣現象
                @Json(name = "WindDirection")
                val windDirection: Float = 0.0f, //平均風風向
                @Json(name = "WindSpeed")
                val windSpeed: Float = 0.0f //平均風風速
            ) {
                data class DailyExtreme(
                    @Json(name = "DailyHigh")
                    val dailyHigh: DailyHigh = DailyHigh(),
                    @Json(name = "DailyLow")
                    val dailyLow: DailyLow = DailyLow()
                ) {
                    data class DailyHigh(
                        @Json(name = "TemperatureInfo")
                        val temperatureInfo: TemperatureInfo = TemperatureInfo()
                    ) {
                        data class TemperatureInfo(
                            @Json(name = "AirTemperature")
                            val airTemperature: Float = 0.0f, //當日最高溫
                            @Json(name = "Occurred_at")
                            val occurredAt: OccurredAt = OccurredAt()
                        ) {
                            data class OccurredAt(
                                @Json(name = "DateTime")
                                val dateTime: String = "" //當日最高溫發生時間
                            )

                        }
                    }

                    data class DailyLow(
                        @Json(name = "TemperatureInfo")
                        val temperatureInfo: TemperatureInfo = TemperatureInfo()
                    ) {
                        data class TemperatureInfo(
                            @Json(name = "AirTemperature")
                            val airTemperature: Double = 0.0, //當日最低溫
                            @Json(name = "Occurred_at")
                            val occurredAt: OccurredAt = OccurredAt()
                        ) {
                            data class OccurredAt(
                                @Json(name = "DateTime")
                                val dateTime: String = "" //當日最低溫發生時間
                            )
                        }
                    }
                }

                data class GustInfo(
                    @Json(name = "Occurred_at")
                    val occurredAt: WindOccurredAt = WindOccurredAt(),
                    @Json(name = "PeakGustSpeed")
                    val peakGustSpeed: Float = 0.0f //最大瞬間風風速
                ) {
                    data class WindOccurredAt(
                        @Json(name = "DateTime")
                        val dateTime: String = "",
                        @Json(name = "WindDirection")
                        val windDirection: Int = 0 //最大瞬間風發生風向
                    )
                }

                data class Max10MinAverage(
                    @Json(name = "Occurred_at")
                    val occurredAt: WindOccurredAt = WindOccurredAt(),
                    @Json(name = "WindSpeed")
                    val windSpeed: Double = 0.0 //最大10分鐘平均風速
                ) {
                    data class WindOccurredAt(
                        @Json(name = "DateTime")
                        val dateTime: String = "",
                        @Json(name = "WindDirection")
                        val windDirection: Int = 0 //最大10分鐘平均風向
                    )
                }

                data class Now(
                    @Json(name = "Precipitation")
                    val precipitation: Float = 0.0f //當日降水量
                )
            }
        }
    }

    data class Result(
        val fields: List<Field> = listOf(),
        @Json(name = "resource_id")
        val resourceId: String = ""
    ) {
        data class Field(
            val id: String = "",
            val type: String = ""
        )
    }
}