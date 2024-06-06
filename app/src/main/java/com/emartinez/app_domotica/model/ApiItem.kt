package com.emartinez.app_domotica.model

/**
 * Clase sellada `ApiItem` que representa diferentes tipos de elementos de la API.
 * Cada subclase de `ApiItem` representa un tipo específico de elemento de la API.
 */
sealed class ApiItem {

    data class Light(val entityId: String, val state: String) : ApiItem()
    data class OpeningSensor(val entityId: String, val state: String) : ApiItem()
    data class BatterySensor(val entityId: String, val state: Float?) : ApiItem()
    data class TemperatureSensor(val entityId: String, val state: Float?) : ApiItem()
    data class Camera(val entityId: String, val state: String) : ApiItem()
}