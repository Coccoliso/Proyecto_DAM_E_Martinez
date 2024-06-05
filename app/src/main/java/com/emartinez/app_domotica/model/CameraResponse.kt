package com.emartinez.app_domotica.model

import com.google.gson.annotations.SerializedName

data class CameraResponse(
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("state") val state: String,
    @SerializedName("attributes") val attributes: Attributes
) {
    data class Attributes(
        @SerializedName("is_camera_on") val isCameraOn: Boolean,
        @SerializedName("is_microphone_on") val isMicrophoneOn: Boolean,
        @SerializedName("movement_direction") val movementDirection: String,
        @SerializedName("entity_picture") val entityPicture: String
    )
}
