package com.emartinez.app_domotica.ui

import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.model.ApiService
import com.emartinez.app_domotica.databinding.ItemCameraBinding
import com.emartinez.app_domotica.model.ApiItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CameraViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemCameraBinding.bind(view)

    @OptIn(UnstableApi::class)
    fun bind(camera: ApiItem.Camera) {
        Log.d("CameraViewHolder", "Enlazando cámara: ${camera.entityId}")

        val cameraName = camera.entityId.split(".").last().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        CoroutineScope(Dispatchers.IO).launch {
            val cameraResponse = activity.retrofit.create(ApiService::class.java)
                .getCameraState(camera.entityId)

            withContext(Dispatchers.Main) {
                binding.tvCameraName.text = cameraName
            }
        }
    }
}