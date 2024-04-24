package com.emartinez.app_domotica.client

import com.emartinez.app_domotica.api.HomeAssistantApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HomeAssistantClient {
    private const val BASE_URL = "http://homeassistant.local:8123/"

    fun getClient(): HomeAssistantApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(HomeAssistantApi::class.java)
    }
}
