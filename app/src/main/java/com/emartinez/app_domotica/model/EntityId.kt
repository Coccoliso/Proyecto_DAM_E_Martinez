package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

/**
 * Clase `EntityId` que representa el identificador único de una entidad en la API.
 *
 * @property entityId El identificador único de la entidad.
 */
data class EntityId(
    @SerializedName("entity_id") val entityId: String,
)
