package com.linglingdr00.weathernotification.network

data class WeatherData(
    val records: Records = Records(),
    val result: Result = Result(),
    val success: String = ""
) {
    data class Records(
        val datasetDescription: String = "",
        val location: List<Location> = listOf()
    ) {
        data class Location(
            val locationName: String = "",
            val weatherElement: List<WeatherElement> = listOf()
        ) {
            data class WeatherElement(
                val elementName: String = "",
                val time: List<Time> = listOf()
            ) {
                data class Time(
                    val endTime: String = "",
                    val parameter: Parameter = Parameter(),
                    val startTime: String = ""
                ) {
                    data class Parameter(
                        val parameterName: String = "",
                        val parameterUnit: String = "",
                        val parameterValue: String = ""
                    )
                }
            }
        }
    }

    data class Result(
        val fields: List<Field> = listOf(),
        val resourceId: String = ""
    ) {
        data class Field(
            val id: String = "",
            val type: String = ""
        )
    }
}