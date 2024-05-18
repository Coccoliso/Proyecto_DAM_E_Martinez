package com.emartinez.app_domotica.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @GET("api/states")
    suspend fun getStates():Response<List<ItemStateResponse>>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @GET("api/states/{entity_id}")
    suspend fun getItemState(@Path("entity_id") entity_id: String): Response<ItemStateResponse>


    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @POST("api/services/light/turn_on")
    suspend fun turnOnLight(@Body entity_id: EntityId): Response<Void>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NjAxNGI" +
            "yMGU1MGU0OGFmYTE1NTYyMjA5MmY1NzE2ZCIsImlhdCI6MTcxMzk1NzU1MCwiZXhwIjoyMDI5MzE3NTUwfQ" +
            ".pFm-4QjUL7508u6Bfer9rrMNULuF8m7X4dN9yf9n6nY", "Content-Type: application/json")
    @POST("api/services/light/turn_off")
    suspend fun turnOffLight(@Body entity_id: EntityId): Response<Void>
}