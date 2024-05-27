package com.emartinez.app_domotica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toolbar
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
import com.emartinez.app_domotica.recyclerview.OpeningSensorAdapter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.recyclerview.widget.ConcatAdapter
import com.emartinez.app_domotica.DetailItemActivity.Companion.EXTRA_ITEM_ID
import com.emartinez.app_domotica.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HomeAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAssistantBinding
    lateinit var retrofit: Retrofit
    private lateinit var lightAdapter: LightAdapter
    private lateinit var openingSensorAdapter: OpeningSensorAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa drawerLayout y navView
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
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu) // Asegúrate de que este recurso exista y sea el correcto
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lights -> {
                    // Manejar selección de luces
                }
                R.id.nav_sensors -> {
                    // Manejar selección de sensores
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