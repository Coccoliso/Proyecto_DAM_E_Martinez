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
    fun bind(light: ApiItem.Light, onItemSelected: (String)->Unit) {
        Log.d("LightViewHolder", "Enlazando luz: ${light.entityId}")

        val lightName = light.entityId.split(".").last()
        binding.tvLightId.text = lightName
        checkLightState(light)

        // Genera un ID único para el switch de esta luz
        binding.swLight.id = View.generateViewId()

        binding.swLight.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.Main).launch {
                val result = changeLightState(light.entityId, isChecked)
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

    private suspend fun changeLightState(entityId: String, state: Boolean): Boolean {
        return CoroutineScope(Dispatchers.IO).async {
            val myResponse = if (state) {
                activity.retrofit.create(ApiService::class.java)
                    .turnOnLight(EntityId(entityId)).execute()
            } else {
                activity.retrofit.create(ApiService::class.java)
                    .turnOffLight(EntityId(entityId)).execute()
            }
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    if (state) {
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    } else {
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                    true
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                    false
                }
            }
        }.await()
    }


}