package com.emartinez.app_domotica.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.model.ApiService
import com.emartinez.app_domotica.databinding.ActivityOpclSensorBinding
import com.emartinez.app_domotica.model.ApiItem
import com.emartinez.app_domotica.ui.recyclerview.OpclSensorAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpclSensorActivity : HomeAssistantActivity() {

    private lateinit var binding: ActivityOpclSensorBinding
    private lateinit var opclSensorAdapter: OpclSensorAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opcl_sensor)

        binding = ActivityOpclSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = findViewById<Toolbar>(R.id.opcl_sensor_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon?.setTint(getColor(R.color.title_text))

        initUi()
        fetchApiData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUi(){
        opclSensorAdapter = OpclSensorAdapter(this)
        binding.rvOpclSensorItemList.adapter = opclSensorAdapter

        binding.rvOpclSensorItemList.setHasFixedSize(true)
        binding.rvOpclSensorItemList.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchApiData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java).getStates().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("HomeAssistant", "Respuesta exitosa: ${response.body()}")
                    val apiItems = classifyApiResponse(response.body()!!)
                    val openingSensors = apiItems.filterIsInstance<ApiItem.OpeningSensor>()
                    Log.d("HomeAssistant", "Número de sensores de apertura: ${openingSensors.size}")

                    opclSensorAdapter.clear()
                    opclSensorAdapter.updateList(openingSensors)
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                }
            }
        }
    }
}