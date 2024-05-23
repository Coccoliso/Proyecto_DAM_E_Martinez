package com.emartinez.app_domotica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.ItemStateResponse
import com.emartinez.app_domotica.databinding.ActivityHomeAssistantBinding
import com.emartinez.app_domotica.recyclerview.ApiItem
import com.emartinez.app_domotica.recyclerview.LightAdapter
import com.emartinez.app_domotica.recyclerview.OpeningSensorAdapter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.recyclerview.widget.ConcatAdapter
import com.emartinez.app_domotica.DetailItemActivity.Companion.EXTRA_ITEM_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HomeAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAssistantBinding
    lateinit var retrofit: Retrofit
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
        lightAdapter = LightAdapter(this){navigateToDetailItemActivity(it)}
        openingSensorAdapter = OpeningSensorAdapter(this){navigateToDetailItemActivity(it)}
        val concatAdapter = ConcatAdapter(lightAdapter, openingSensorAdapter)

        binding.rvItemList.setHasFixedSize(true)
        binding.rvItemList.layoutManager = LinearLayoutManager(this)
        binding.rvItemList.adapter = concatAdapter
    }

    private fun classifyApiResponse(response: List<ItemStateResponse>): List<ApiItem> {
        val apiItems = mutableListOf<ApiItem>()

        for (item in response) {
            val sensorName = item.entityId.split(".").last()
            if (sensorName.isNotBlank()) {  // Verifica que el nombre no esté vacío
                when {
                    item.entityId.startsWith("light") -> apiItems.add(ApiItem.Light(item.entityId, item.state))
                    item.entityId.startsWith("binary_sensor") -> apiItems.add(ApiItem.OpeningSensor(item.entityId, item.state))
                    item.entityId.endsWith("_battery") -> apiItems.add(ApiItem.BatterySensor(item.entityId, item.state.toFloatOrNull()))
                    item.entityId.endsWith("_temperature") -> apiItems.add(ApiItem.TemperatureSensor(item.entityId, item.state.toFloatOrNull()))
                }
            }
        }

        return apiItems
    }

    private fun fetchApiData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java).getStates().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("HomeAssistant", "Respuesta exitosa: ${response.body()}")
                    val apiItems = classifyApiResponse(response.body()!!)
                    val lights = apiItems.filterIsInstance<ApiItem.Light>()
                    val openingSensors = apiItems.filterIsInstance<ApiItem.OpeningSensor>()
                    val batterySensors = apiItems.filterIsInstance<ApiItem.BatterySensor>()
                    val temperatureSensors = apiItems.filterIsInstance<ApiItem.TemperatureSensor>()
                    Log.d("HomeAssistant", "Número de luces: ${lights.size}")
                    Log.d("HomeAssistant", "Número de sensores de apertura: ${openingSensors.size}")
                    lightAdapter.clear()  // Limpia los datos antiguos
                    lightAdapter.updateList(lights)
                    openingSensorAdapter.clear()  // Limpia los datos antiguos
                    openingSensorAdapter.updateList(openingSensors)
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                }
            }
        }
    }

    private fun navigateToDetailItemActivity(id: String) {
        val intent = Intent(this, DetailItemActivity::class.java)
        intent.putExtra(EXTRA_ITEM_ID, id)
        startActivity(intent)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://homeassistant.local:8123/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}