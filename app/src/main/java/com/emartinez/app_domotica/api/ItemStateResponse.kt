package com.emartinez.app_domotica.api

import com.google.gson.annotations.SerializedName

data class ItemStateResponse(
    @SerializedName ("entity_id") val entityId: String,
    @SerializedName ("state") val state: String,
)