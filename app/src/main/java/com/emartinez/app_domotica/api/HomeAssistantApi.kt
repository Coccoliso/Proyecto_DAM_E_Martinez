package com.emartinez.app_domotica.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface HomeAssistantApi {
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @GET("api/states")
    fun getStates(): Call<List<Entity>>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun turnOnLight(@Body body: TurnOnOffLightRequest): Call<Void>
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @POST("api/services/light/turn_off")
    fun turnOffLight(@Body body: TurnOnOffLightRequest): Call<Void>

}
