package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

/**
 * Clase `LightResponse` que representa la respuesta de la API al solicitar el estado de una luz.
 *
 * @property entityId El identificador único de la luz en la API.
 * @property state El estado actual de la luz.
 * @property attributes Los atributos adicionales de la luz, que incluyen el brillo y el color RGB.
 */
data class LightResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: Attributes
) {
    /**
     * Clase `Attributes` que representa los atributos adicionales de la luz.
     *
     * @property brightness El brillo actual de la luz.
     * @property rgbColor El color RGB actual de la luz.
     */
    data class Attributes(
        @SerializedName("brightness") val brightness: Int,
        @SerializedName("rgb_color") val rgbColor: List<Int>
    )
}

/**
 * Clase `LightResponse` que representa la respuesta de la API al solicitar el color de una luz.
 *
 * @property entityId El identificador único de la luz en la API.
 * @property rgbColor El color actual de la luz.
 */
data class ChangeColorBody(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("rgb_color") val rgbColor: List<Int>
)

/**
 * Clase `LightResponse` que representa la respuesta de la API al solicitar el brillo de una luz.
 *
 * @property entityId El identificador único de la luz en la API.
 * @property brightness El brillo actual de la luz.
 */
data class ChangeBrightnessBody(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("brightness") val brightness: Int
)

