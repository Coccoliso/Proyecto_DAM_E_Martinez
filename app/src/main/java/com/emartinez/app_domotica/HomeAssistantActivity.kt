package com.emartinez.app_domotica

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.ItemStateResponse
import com.emartinez.app_domotica.recyclerview.HomeAssistantAdapter
import com.emartinez.app_domotica.databinding.ActivityHomeAssistantBinding
import com.emartinez.app_domotica.recyclerview.ApiItem
import com.emartinez.app_domotica.recyclerview.LightAdapter
import com.emartinez.app_domotica.recyclerview.OpeningSensorAdapter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.recyclerview.widget.ConcatAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HomeAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAssistantBinding
    lateinit var retrofit: Retrofit
    private lateinit var adapter: HomeAssistantAdapter
    private lateinit var lightAdapter: LightAdapter
    private lateinit var openingSensorAdapter: OpeningSensorAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = createRetrofit()
        initUi()
        lifecycleScope.launch {
            fetchApiData()
        }

    }

    private fun initUi() {
        lightAdapter = LightAdapter(this)
        openingSensorAdapter = OpeningSensorAdapter(this)
        val concatAdapter = ConcatAdapter(lightAdapter, openingSensorAdapter)

        binding.rvItemList.setHasFixedSize(true)
        binding.rvItemList.layoutManager = LinearLayoutManager(this)
        binding.rvItemList.adapter = concatAdapter
    }

    private fun classifyApiResponse(response: List<ItemStateResponse>): List<ApiItem> {
        val apiItems = mutableListOf<ApiItem>()

        for (item in response) {
            when {
                item.entityId.startsWith("light") -> apiItems.add(ApiItem.Light(item.entityId, item.state))
                item.entityId.startsWith("sensor.sensordsfsd") -> apiItems.add(ApiItem.OpeningSensor(item.entityId, item.state))
            }
        }

        return apiItems
    }

    private fun fetchApiData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java).getStates().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val apiItems = classifyApiResponse(response.body()!!)
                    val lights = apiItems.filterIsInstance<ApiItem.Light>()
                    val openingSensors = apiItems.filterIsInstance<ApiItem.OpeningSensor>()
                    Log.d("HomeAssistant", "Número de luces: ${lights.size}")
                    Log.d("HomeAssistant", "Número de sensores de apertura: ${openingSensors.size}")
                    lightAdapter.updateList(lights)
                    openingSensorAdapter.updateList(openingSensors)
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                }
            }
        }
    }



    private fun createRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://homeassistant.local:8123/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}