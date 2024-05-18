package com.emartinez.app_domotica

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.EntityId
import com.emartinez.app_domotica.recyclerview.HomeAssistantAdapter
import com.emartinez.app_domotica.databinding.ActivityHomeAssistantBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAssistantBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: HomeAssistantAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = getRetrofit()
        checkState()
        initUi()
        checkAllItemState()

    }

    private fun initUi() {
        binding.swLight.setOnCheckedChangeListener { _, isChecked ->
            changeLightState(isChecked)
        }

        adapter = HomeAssistantAdapter()
        binding.rvItemList.setHasFixedSize(true)
        binding.rvItemList.layoutManager = LinearLayoutManager(this)
        binding.rvItemList.adapter = adapter



    }

    private fun checkState() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                retrofit.create(ApiService::class.java).getItemState("light.habitacion_superior")
            if (myResponse.isSuccessful) {
                val response = myResponse.body()
                Log.i("HomeAssistant", "Conexión exitosa. Entidades: ${response.toString()}")
                val state = response?.state
                Log.i("HomeAssistant", "Estado: $state")

                if (state == "on") {
                    runOnUiThread {
                        binding.swLight.isChecked = true
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    }
                } else {
                    runOnUiThread {
                        binding.swLight.isChecked = false
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                }
            } else {
                Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
            }
        }
    }

    private fun checkAllItemState() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                retrofit.create(ApiService::class.java).getStates()
            if (myResponse.isSuccessful) {
                val response = myResponse.body()
                if (response != null) {
                    runOnUiThread() {
                        adapter.updateList(response)
                    }
                }
            } else {
                Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
            }
        }
    }

    private fun changeLightState(state: Boolean) {
        if (state) {
            CoroutineScope(Dispatchers.IO).launch {
                val myResponse = retrofit.create(ApiService::class.java)
                    .turnOnLight(EntityId("light.habitacion_superior"))
                if (myResponse.isSuccessful) {
                    Log.i("HomeAssistant", "Encendido exitoso")
                    runOnUiThread {
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    }
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val myResponse = retrofit.create(ApiService::class.java)
                    .turnOffLight(EntityId("light.habitacion_superior"))
                if (myResponse.isSuccessful) {
                    Log.i("HomeAssistant", "Apagado exitoso")
                    runOnUiThread {
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                } else {
                    Log.e(
                        "HomeAssistant",
                        "Error en la conexión: ${myResponse.errorBody()?.string()}"
                    )
                }
            }
        }
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://homeassistant.local:8123/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}