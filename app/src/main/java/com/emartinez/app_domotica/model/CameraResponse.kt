package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

data class CameraResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: Attributes
) {
    data class Attributes(
        @SerializedName("access_token") val accessToken: String,

    )
}
