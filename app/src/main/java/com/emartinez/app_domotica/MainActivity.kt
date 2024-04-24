package com.emartinez.app_domotica

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emartinez.app_domotica.api.Entity
import com.emartinez.app_domotica.services.HomeAssistantService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var ivLight: ImageView
    private lateinit var swLight: SwitchCompat

    private val homeAssistantService = HomeAssistantService()
    private var isLightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        conectionCheck()
        initComponent()
        initListeners()

    }

    private fun initListeners() {
        swLight.setOnCheckedChangeListener { _, isChecked ->
            turnOnOffLight(isChecked)
        }
    }

    private fun initComponent() {
        ivLight = findViewById(R.id.ivLight)
        swLight = findViewById(R.id.swLight)
    }

    private fun conectionCheck() {
        homeAssistantService.getStates(object : Callback<List<Entity>> {
            override fun onResponse(call: Call<List<Entity>>, response: Response<List<Entity>>) {
                if (response.isSuccessful) {
                    val entities = response.body()
                    Log.d("HomeAssistant", "Conexión exitosa. Entidades: $entities")
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Entity>>, t: Throwable) {
                Log.e("HomeAssistant", "Error en la conexión: ${t.message}")
            }
        })
    }

    private fun turnOnOffLight(isLightOn: Boolean){
        if (isLightOn) {
            homeAssistantService.turnOnLight("light.habitacion_superior", object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("HomeAssistant", "Luz encendida con éxito.")
                    } else {
                        Log.e("HomeAssistant", "Error al encender la luz: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("HomeAssistant", "Error al encender la luz: ${t.message}")
                }
            })
        } else {
            homeAssistantService.turnOffLight("light.habitacion_superior", object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("HomeAssistant", "Luz apagada con éxito.")
                    } else {
                        Log.e("HomeAssistant", "Error al apagar la luz: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("HomeAssistant", "Error al apagar la luz: ${t.message}")
                }
            })
        }
        ivLight.setImageResource(if (isLightOn) R.drawable.ic_light_on else R.drawable.ic_light_off)
    }
}