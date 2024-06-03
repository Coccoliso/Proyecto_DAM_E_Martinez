package com.emartinez.app_domotica

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.ItemStateResponse
import com.emartinez.app_domotica.databinding.ActivityHomeAssistantBinding
import com.emartinez.app_domotica.recyclerview.ApiItem
import com.emartinez.app_domotica.recyclerview.LightAdapter
import com.emartinez.app_domotica.recyclerview.OpclSensorAdapter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.emartinez.app_domotica.api.Auth
import com.emartinez.app_domotica.login.LoginActivity
import com.emartinez.app_domotica.recyclerview.CameraAdapter
import com.emartinez.app_domotica.settings.SettingsActivity
import com.emartinez.app_domotica.ui.CameraActivity
import com.emartinez.app_domotica.ui.LightActivity
import com.emartinez.app_domotica.ui.OpclSensorActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException


open class HomeAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAssistantBinding
    lateinit var retrofit: Retrofit
    private lateinit var lightAdapter: LightAdapter
    private lateinit var openingSensorAdapter: OpclSensorAdapter
    private lateinit var cameraAdapter: CameraAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)


        drawerLayout = binding.drawerLayout
        navView = binding.navView

        retrofit = createRetrofit()
        initUi()
        lifecycleScope.launch {
            fetchApiData()
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = object : ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                syncState()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                syncState()
            }
        }

        toggle.isDrawerIndicatorEnabled = true
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lights -> {
                    val intent = Intent(this, LightActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_sensors -> {
                    val intent = Intent(this, OpclSensorActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_cameras -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_options -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun initUi() {
        lightAdapter = LightAdapter(this)
        openingSensorAdapter = OpclSensorAdapter(this)
        cameraAdapter = CameraAdapter(this)
        val concatAdapter = ConcatAdapter(lightAdapter, openingSensorAdapter, cameraAdapter)

        binding.rvItemList.setHasFixedSize(true)
        binding.rvItemList.layoutManager = LinearLayoutManager(this)
        binding.rvItemList.adapter = concatAdapter

        loadToken()
    }

    fun classifyApiResponse(response: List<ItemStateResponse>): List<ApiItem> {
        val apiItems = mutableListOf<ApiItem>()

        for (item in response) {
            val sensorName = item.entityId.split(".").last()
            if (sensorName.isNotBlank()) {
                when {
                    item.entityId.startsWith("light") -> apiItems.add(
                        ApiItem.Light(
                            item.entityId,
                            item.state
                        )
                    )

                    item.entityId.startsWith("binary_sensor") -> apiItems.add(
                        ApiItem.OpeningSensor(
                            item.entityId,
                            item.state
                        )
                    )

                    item.entityId.startsWith("camera") -> apiItems.add(
                        ApiItem.Camera(
                            item.entityId,
                            item.state
                        )
                    )

                    item.entityId.endsWith("_battery") -> apiItems.add(
                        ApiItem.BatterySensor(
                            item.entityId,
                            item.state.toFloatOrNull()
                        )
                    )

                    item.entityId.endsWith("_temperature") -> apiItems.add(
                        ApiItem.TemperatureSensor(
                            item.entityId,
                            item.state.toFloatOrNull()
                        )
                    )
                }
            }
        }

        return apiItems
    }

    @SuppressLint("SetTextI18n")
    private fun fetchApiData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiService::class.java).getStates().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("HomeAssistant", "Respuesta exitosa: ${response.body()}")
                        val apiItems = classifyApiResponse(response.body()!!)
                        val lights = apiItems.filterIsInstance<ApiItem.Light>()
                        val openingSensors = apiItems.filterIsInstance<ApiItem.OpeningSensor>()
                        val cameras = apiItems.filterIsInstance<ApiItem.Camera>()
                        Log.d("HomeAssistant", "Número de luces: ${lights.size}")
                        Log.d(
                            "HomeAssistant",
                            "Número de sensores de apertura: ${openingSensors.size}"
                        )
                        Log.d("HomeAssistant", "Número de cámaras: ${cameras.size}")
                        lightAdapter.clear()
                        lightAdapter.updateList(lights)
                        openingSensorAdapter.clear()
                        openingSensorAdapter.updateList(openingSensors)
                        cameraAdapter.clear()
                        cameraAdapter.updateList(cameras)
                    } else {
                        Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                    }
                }
                val weatherResponse =
                    retrofit.create(ApiService::class.java).getWeatherState("weather.forecast_casa")
                withContext(Dispatchers.Main) {
                    Log.d("HomeAssistant", "Estado del tiempo: ${weatherResponse.state}")
                    Log.d("HomeAssistant", "Temperatura: ${weatherResponse.attributes.temperature}")
                    Log.d(
                        "HomeAssistant",
                        "Presión del aire: ${weatherResponse.attributes.pressure}"
                    )
                    Log.d("HomeAssistant", "Humedad: ${weatherResponse.attributes.humidity}")
                    Log.d(
                        "HomeAssistant",
                        "Velocidad del viento: ${weatherResponse.attributes.windSpeed}"
                    )

                    val translatedState = translateWeatherState(weatherResponse.state)
                    binding.tvWeather.text = translatedState
                    binding.tvTemperature.text = "${weatherResponse.attributes.temperature}°C"
                    binding.tvPressure.text = "${weatherResponse.attributes.pressure} hPa"
                    binding.tvHumidity.text = "${weatherResponse.attributes.humidity}%"
                    binding.tvWindSpeed.text = "${weatherResponse.attributes.windSpeed} km/h"
                }
            }catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.e("HomeAssistant", "Error de token de autenticación: ${e.message()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@HomeAssistantActivity,
                            "Introduce un token de autenticación válido",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(this@HomeAssistantActivity, SettingsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${e.message()}")
                }
            }
        }
    }

    private fun createRetrofit(): Retrofit {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${Auth.token}")
                .build()
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit
            .Builder()
            .baseUrl("https://uqhxult1i7sr8yupf6tljeton2wctfsq.ui.nabu.casa/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun translateWeatherState(state: String): String {
        return when (state) {
            "clear-night" -> "Despejado"
            "cloudy" -> "Nublado"
            "fog" -> "Niebla"
            "hail" -> "Granizo"
            "lightning" -> "Relámpagos"
            "lightning-rainy" -> "Lluvia con relámpagos"
            "partlycloudy" -> "Parcialmente nublado"
            "pouring" -> "Lluvia torrencial"
            "rainy" -> "Lluvioso"
            "snowy" -> "Nevado"
            "snowy-rainy" -> "Nieve con lluvia"
            "sunny" -> "Soleado"
            "windy" -> "Ventoso"
            "windy-variant" -> "Ventoso variante"
            "exceptional" -> "Excepcional"
            else -> "Estado desconocido"
        }
    }
    private fun loadToken() {
        val sharedPref = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        Auth.token = sharedPref.getString(SettingsActivity.KEY_TOKEN, "") ?: ""
    }

}