package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

/**
 * Clase `ItemStateResponse` representa la respuesta de la API al solicitar el estado de un elemento.
 *
 * @property entityId El identificador único del elemento en la API.
 * @property state El estado actual del elemento.
 */
data class ItemStateResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
)