package com.emartinez.app_domotica.ui.recyclerview

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.model.ApiService
import com.emartinez.app_domotica.databinding.ItemCameraBinding
import com.emartinez.app_domotica.model.ApiItem
import com.emartinez.app_domotica.model.CameraResponse
import com.emartinez.app_domotica.ui.VideoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Clase `CameraViewHolder` que se encarga de proporcionar la vista para un elemento de la lista de cámaras en la interfaz de usuario.
 *
 * @property View La vista que representa un elemento de la lista de cámaras.
 * @property activity La actividad en la que se utiliza este ViewHolder.
 */
class CameraViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemCameraBinding.bind(view)

    /**
     * Enlaza los datos de una cámara con la vista.
     *
     * @param camera Los datos de la cámara que se enlazarán con la vista.
     */
    @OptIn(UnstableApi::class)
    fun bind(camera: ApiItem.Camera) {
        Log.d("CameraViewHolder", "Enlazando cámara: ${camera.entityId}")

        val cameraName = camera.entityId.split(".").last().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        Log.d("CameraViewHolder", "Nombre de cámara: $cameraName")
        CoroutineScope(Dispatchers.IO).launch {

            val myResponse: Response<CameraResponse> =
                activity.retrofit.create(ApiService::class.java).getCameraState(camera.entityId)
            if (myResponse.isSuccessful) {
                val responseBody = myResponse.body()
                val accessToken = responseBody?.attributes?.accessToken

                binding.tvCameraName.text = cameraName

                withContext(Dispatchers.Main) {
                    if (accessToken != null) {

                        val streamUrl =
                            "https://uqhxult1i7sr8yupf6tljeton2wctfsq.ui.nabu.casa/api/camera_proxy_stream/${camera.entityId}?token=${accessToken}"
                        Log.d("CameraViewHolder", "URL de stream: $streamUrl")

                        binding.llCamera.setOnClickListener {
                            val intent = Intent(activity, VideoActivity::class.java)
                            intent.putExtra("streamUrl", streamUrl)
                            activity.startActivity(intent)
                        }

                    } else {
                        Log.e("CameraViewHolder", "Access token is null")
                    }
                }
            } else {
                Log.e("CameraViewHolder", "API response is not successful")
            }
        }
    }
}