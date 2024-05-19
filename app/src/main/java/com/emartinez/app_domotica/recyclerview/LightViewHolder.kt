package com.emartinez.app_domotica.recyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.EntityId
import com.emartinez.app_domotica.databinding.ItemLightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LightViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {


    private val binding = ItemLightBinding.bind(view)
    fun bind(light: ApiItem.Light) {
        Log.d("LightViewHolder", "Enlazando luz: ${light.entityId}")
        binding.tvLightId.text = light.entityId
        checkLightState(light)

        binding.swLight.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.Main).launch {
                val result = changeLightState(isChecked)
                if (result) {
                    binding.ivLight.setImageResource(R.drawable.ic_light_on)
                } else {
                    binding.ivLight.setImageResource(R.drawable.ic_light_off)
                }
            }
        }
    }

    private fun checkLightState(item: ApiItem.Light) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                activity.retrofit.create(ApiService::class.java).getItemState(item.entityId).execute()
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    val response = myResponse.body()
                    val state = response?.state
                    Log.d("LightViewHolder", "Estado de la luz ${item.entityId}: $state")
                    binding.swLight.isChecked = state == "on"
                    if (state == "on") {
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    } else {
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                }
            }
        }
    }

    private fun changeLightState(state: Boolean): Boolean {
        var result = false
        CoroutineScope(Dispatchers.IO).async {
            val myResponse = if (state) {
                activity.retrofit.create(ApiService::class.java)
                    .turnOnLight(EntityId("light.habitacion_superior")).execute()
            } else {
                activity.retrofit.create(ApiService::class.java)
                    .turnOffLight(EntityId("light.habitacion_superior")).execute()
            }
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    if (state) {
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    } else {
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                    result = true
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                    result = false
                }
            }
        }.start()
        return result
    }


}