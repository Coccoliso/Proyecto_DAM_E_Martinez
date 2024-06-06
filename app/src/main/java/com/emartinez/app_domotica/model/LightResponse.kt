package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

data class LightResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: Attributes
){
    data class Attributes(
        @SerializedName("brightness") val brightness: Int,
        @SerializedName("rgb_color") val rgbColor: List<Int>
    )
}

data class ChangeColorBody(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("rgb_color") val rgbColor: List<Int>
)

data class ChangeBrightnessBody(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("brightness") val brightness: Int
)

