package com.linglingdr00.weather.network

data class ForecastData(
    val records: Records = Records(),
    val result: Result = Result(),
    val success: String = "" //true
) {
    data class Records(
        val datasetDescription: String = "", //三十六小時天氣預報
        val location: List<Location> = listOf()
    ) {
        data class Location(
            val locationName: String = "", //嘉義縣
            val weatherElement: List<WeatherElement> = listOf()
        ) {
            data class WeatherElement(
                val elementName: String = "", //Wx(天氣現象), PoP(降雨機率), MinT, CI(舒適度), MaxT
                val time: List<Time> = listOf()
            ) {
                data class Time(
                    val endTime: String = "", //2024-01-22 06:00:00
                    val parameter: Parameter = Parameter(),
                    val startTime: String = "" //2024-01-21 18:00:00
                ) {
                    data class Parameter(
                        val parameterName: String = "", //多雲時陰, 10
                        val parameterUnit: String = "", //百分比, C,
                        val parameterValue: String = "" //5
                    )
                }
            }
        }
    }

    data class Result(
        val fields: List<Field> = listOf(),
        val resourceId: String = "" //F-C0032-001
    ) {
        data class Field(
            val id: String = "", //datasetDescription
            val type: String = "" //String
        )
    }
}