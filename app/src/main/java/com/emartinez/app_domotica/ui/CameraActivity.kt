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
import com.emartinez.app_domotica.databinding.ActivityCameraBinding
import com.emartinez.app_domotica.model.ApiItem
import com.emartinez.app_domotica.ui.recyclerview.CameraAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `CameraActivity` es una actividad que proporciona la interfaz de usuario para la gestión de cámaras en la aplicación.
 *
 * @property binding El objeto de enlace que da acceso a las vistas en el diseño.
 * @property cameraAdapter El adaptador para la lista de cámaras.
 */
class CameraActivity : HomeAssistantActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraAdapter: CameraAdapter

    /**
     * Método que se llama al crear la actividad. Inicializa la interfaz de usuario y recupera los datos de la API.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = findViewById<Toolbar>(R.id.camera_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon?.setTint(getColor(R.color.title_text))

        initUi()
        fetchApiData()
    }

    /**
     * Método que se llama cuando se selecciona un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método para inicializar la interfaz de usuario. Establece el adaptador para la lista de cámaras.
     */
    private fun initUi() {
        cameraAdapter = CameraAdapter(this)
        binding.rvCameraItemList.adapter = cameraAdapter

        binding.rvCameraItemList.setHasFixedSize(true)
        binding.rvCameraItemList.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Método para recuperar los datos de la API. Clasifica la respuesta de la API y actualiza la lista de cámaras.
     */
    private fun fetchApiData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java).getStates().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("HomeAssistant", "Respuesta exitosa: ${response.body()}")
                    val apiItems = classifyApiResponse(response.body()!!)
                    val cameras = apiItems.filterIsInstance<ApiItem.Camera>()
                    Log.d("HomeAssistant", "Número de cámaras: ${cameras.size}")
                    cameraAdapter.clear()
                    cameraAdapter.updateList(cameras)
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                }
            }
        }
    }
}