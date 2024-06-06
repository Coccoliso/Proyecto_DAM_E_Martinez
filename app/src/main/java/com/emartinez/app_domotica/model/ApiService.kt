package com.emartinez.app_domotica.model

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @Headers("Content-Type: application/json")
    @GET("api/states")
    fun getStates(): Call<List<ItemStateResponse>>

    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    fun getItemState(@Path("entity_id") entityId: String): Call<ItemStateResponse>

    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    fun getLightState(@Path("entity_id") entityId: String): Call<LightResponse>

    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun turnOnLight(@Body entityId: EntityId): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_off")
    fun turnOffLight(@Body entityId: EntityId): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun changeLightColor(@Body body: ChangeColorBody): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun changeLightBrightness(@Body body: ChangeBrightnessBody): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    suspend fun getWeatherState(@Path("entity_id") entityId: String): WeatherResponse

    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    suspend fun getCameraState(@Path("entity_id") entityId: String): Response<CameraResponse>

    @Headers("Content-Type: application/json")
    @GET("api/camera_proxy/{entity_id}")
    suspend fun getCameraStream(@Path("entity_id") entityId: String): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("api/camera_proxy_stream/{entity_id}")
    suspend fun getCameraVideoStream(@Path("entity_id") entityId: String): Response<ResponseBody>
}