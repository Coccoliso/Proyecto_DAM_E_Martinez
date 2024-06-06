package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

/**
 * Clase `WeatherResponse` que representa la respuesta de la API al solicitar el estado del clima.
 *
 * @property entityId El identificador único del elemento en la API.
 * @property state El estado actual del clima.
 * @property attributes Los atributos adicionales del clima, que incluyen la temperatura, la presión, la humedad y la velocidad del viento.
 */
data class WeatherResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: WeatherAttributes
)

/**
 * Clase `WeatherAttributes` que representa los atributos adicionales del clima.
 *
 * @property temperature La temperatura actual.
 * @property pressure La presión atmosférica actual.
 * @property humidity La humedad actual.
 * @property windSpeed La velocidad del viento actual.
 */
data class WeatherAttributes(
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("pressure") val pressure: Float,
    @SerializedName("humidity") val humidity: Float,
    @SerializedName("wind_speed") val windSpeed: Float
)
