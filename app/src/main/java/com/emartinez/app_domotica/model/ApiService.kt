package com.emartinez.app_domotica.model

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz `ApiService` que define los endpoints de la API.
 *
 * Esta interfaz utiliza Retrofit para definir y realizar las solicitudes HTTP a la API.
 * Cada método representa un endpoint de la API y puede devolver un objeto `Call` o `Response` que
 * encapsula la solicitud y la respuesta.
 */
interface ApiService {

    /**
     * Obtiene el estado de todos los elementos.
     *
     * @return Una lista de respuestas de estado de elementos.
     */
    @Headers("Content-Type: application/json")
    @GET("api/states")
    fun getStates(): Call<List<ItemStateResponse>>

    /**
     * Obtiene el estado del item que se pasa por parámetro.
     *
     * @param entityId El identificador único del elemento en la API.
     * @return Una respuesta de estado del elemento.
     */
    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    fun getItemState(@Path("entity_id") entityId: String): Call<ItemStateResponse>

    /**
     * Obtiene el estado de la luz que se pasa por parámetro.
     *
     * @param entityId El identificador único de la luz en la API.
     * @return Una respuesta de estado de la luz.
     */
    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    fun getLightState(@Path("entity_id") entityId: String): Call<LightResponse>

    /**
     * Enciende la luz que se pasa por parámetro.
     *
     * @param entityId El identificador único de la luz en la API.
     * @return Una respuesta vacía.
     */
    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun turnOnLight(@Body entityId: EntityId): Call<Void>

    /**
     * Apaga la luz que se pasa por parámetro.
     *
     * @param entityId El identificador único de la luz en la API.
     * @return Una respuesta vacía.
     */
    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_off")
    fun turnOffLight(@Body entityId: EntityId): Call<Void>

    /**
     * Cambia el color de la luz que se pasa por parámetro.
     *
     * @param body El cuerpo de la solicitud que contiene la información necesaria para cambiar el color de la luz.
     * @return Una respuesta vacía.
     */
    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun changeLightColor(@Body body: ChangeColorBody): Call<Void>

    /**
     * Cambia el brillo de la luz que se pasa por parámetro.
     *
     * @param body El cuerpo de la solicitud que contiene la información necesaria para cambiar el brillo de la luz.
     * @return Una respuesta vacía.
     */
    @Headers("Content-Type: application/json")
    @POST("api/services/light/turn_on")
    fun changeLightBrightness(@Body body: ChangeBrightnessBody): Call<Void>

    /**
     * Obtiene el estado del clima que se pasa por parámetro.
     *
     * @param entityId El identificador único del clima en la API.
     * @return Una respuesta de estado del clima.
     */
    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    suspend fun getWeatherState(@Path("entity_id") entityId: String): WeatherResponse

    /**
     * Obtiene el estado de la cámara que se pasa por parámetro.
     *
     * @param entityId El identificador único de la cámara en la API.
     * @return Una respuesta de estado de la cámara.
     */
    @Headers("Content-Type: application/json")
    @GET("api/states/{entity_id}")
    suspend fun getCameraState(@Path("entity_id") entityId: String): Response<CameraResponse>

}