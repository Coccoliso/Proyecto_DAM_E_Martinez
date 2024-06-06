package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

/**
 * Clase `CameraResponse` representa la respuesta de la API al solicitar el estado de una cámara.
 *
 * @property entityId El identificador único de la cámara en la API.
 * @property state El estado actual de la cámara.
 * @property attributes Los atributos adicionales de la cámara, que incluyen el token de acceso.
 */
data class CameraResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: Attributes
) {
    /**
     * Clase `Attributes` representa los atributos adicionales de la cámara.
     *
     * @property accessToken El token de acceso para la cámara.
     */
    data class Attributes(
        @SerializedName("access_token") val accessToken: String,
    )
}