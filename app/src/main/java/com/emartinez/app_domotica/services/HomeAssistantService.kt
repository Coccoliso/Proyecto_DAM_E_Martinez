package com.emartinez.app_domotica.services

import com.emartinez.app_domotica.client.HomeAssistantClient
import com.emartinez.app_domotica.api.Entity
import com.emartinez.app_domotica.api.HomeAssistantApi
import com.emartinez.app_domotica.api.TurnOnOffLightRequest
import retrofit2.Callback

class HomeAssistantService {

    private val api: HomeAssistantApi = HomeAssistantClient.getClient()

    fun getStates(callback: Callback<List<Entity>>) {
        val call = api.getStates()
        call.enqueue(callback)
    }

    fun turnOnLight(entityId: String, callback: Callback<Void>) {
        val body = TurnOnOffLightRequest(entity_id = entityId)
        api.turnOnLight(body).enqueue(callback)
    }

    fun turnOffLight(entityId: String, callback: Callback<Void>) {
        val body = TurnOnOffLightRequest(entity_id = entityId)
        api.turnOffLight(body).enqueue(callback)
    }
}