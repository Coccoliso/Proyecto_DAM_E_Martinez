package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val entityId: String,
    val state: String,
    val attributes: WeatherAttributes
)

data class WeatherAttributes(
    val temperature: Float,
    val pressure: Float,
    val humidity: Float,
    @SerializedName("wind_speed") val windSpeed: Float
)
