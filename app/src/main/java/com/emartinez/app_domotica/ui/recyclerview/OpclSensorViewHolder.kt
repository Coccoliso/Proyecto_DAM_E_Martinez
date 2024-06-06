package com.emartinez.app_domotica.ui.recyclerview

import android.annotation.SuppressLint
import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.model.ApiService
import com.emartinez.app_domotica.model.ItemStateResponse
import com.emartinez.app_domotica.databinding.ItemOpeningSensorBinding
import com.emartinez.app_domotica.model.ApiItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Clase `OpclSensorViewHolder` que se encarga de proporcionar la vista para un elemento de la
 * lista de sensores de apertura en la interfaz de usuario.
 *
 * @property View La vista que representa un elemento de la lista de sensores de apertura.
 * @property activity La actividad en la que se utiliza este ViewHolder.
 */
class OpclSensorViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemOpeningSensorBinding.bind(view)
    private var pollingJob: Job? = null

    /**
     * Enlaza los datos de un sensor de apertura con la vista.
     *
     * @param opclSensor Los datos del sensor de apertura que se enlazarán con la vista.
     */
    fun bind(opclSensor: ApiItem.OpeningSensor) {

        startPollingJob(opclSensor)
        Log.d("OpclSensorViewHolder", "openSensor: $opclSensor")
        Log.d("OpeningSensorViewHolder", "Enlazando sensor de apertura: ${opclSensor.entityId}")
        val sensorName = opclSensor.entityId.split(".").last().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        binding.tvOpeningSensor.text = sensorName

        binding.cvOpeningSensor.setOnClickListener {
            showDialog(opclSensor)
        }

    }

    /**
     * Inicia un trabajo de sondeo para comprobar el estado del sensor de apertura.
     *
     * @param opclSensor Los datos del sensor de apertura que se sondeará.
     */
    private fun startPollingJob(opclSensor: ApiItem.OpeningSensor) {
        pollingJob?.cancel()  // Cancela cualquier pollingJob existente antes de iniciar uno nuevo
        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                checkOpeningSensorState(opclSensor)
                delay(5000)  // Espera 1 segundo antes de la próxima comprobación
            }
        }
    }

    /**
     * Comprueba el estado del sensor de apertura y actualiza la vista en consecuencia.
     *
     * @param item Los datos del sensor de apertura cuyo estado se comprobará.
     */
    private fun checkOpeningSensorState(item: ApiItem.OpeningSensor) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                activity.retrofit.create(ApiService::class.java).getItemState(item.entityId)
                    .execute()
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    val response = myResponse.body()
                    val state = response?.state
                    Log.d(
                        "OpeningSensorViewHolder",
                        "Estado del sensor de apertura ${item.entityId}: $state"
                    )
                    if (state == "on") {
                        binding.ivOpeningSensor.setImageResource(R.drawable.ic_window_open)
                    } else {
                        binding.ivOpeningSensor.setImageResource(R.drawable.ic_window_closed)
                    }
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                }
            }
        }
    }

    /**
     * Muestra un diálogo con información sobre el sensor de apertura.
     *
     * @param opclSensor Los datos del sensor de apertura para el que se mostrará el diálogo.
     */
    @SuppressLint("SetTextI18n")
    private fun showDialog(opclSensor: ApiItem.OpeningSensor) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_opcl_sensor)

        val sensorName = "Sensor " + opclSensor.entityId.split("_").last()
        val sensorBatteryName = "sensor." + opclSensor.entityId.split(".").last() + "_bateria_2"
        val sensorTemperatureName =
            "sensor." + opclSensor.entityId.split(".").last() + "_temperatura_del_dispositivo_2"

        Log.i("OpeningSensorViewHolder", "Mostrando diálogo para el sensor de apertura $sensorName")
        Log.i("OpeningSensorViewHolder", "Sensor de batería: $sensorBatteryName")
        Log.i("OpeningSensorViewHolder", "Sensor de temperatura: $sensorTemperatureName")

        val btnReturn: Button = dialog.findViewById(R.id.btnReturn)
        val tvSensorNameDialog: TextView = dialog.findViewById(R.id.tvSensorNameDialog)
        val tvSensorBattery: TextView = dialog.findViewById(R.id.tvSensorBattery)
        val tvSensorTemperature: TextView = dialog.findViewById(R.id.tvSensorTemperature)

        checkItemState(sensorBatteryName) { batteryState ->
            tvSensorBattery.text = batteryState
        }
        checkItemState(sensorTemperatureName) { temperatureState ->
            tvSensorTemperature.text = temperatureState
        }

        tvSensorNameDialog.text = sensorName


        btnReturn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Comprueba el estado de un elemento (como la batería o la temperatura del sensor de apertura)
     * y devuelve el estado a través de una función de devolución de llamada.
     *
     * @param entityId El identificador único del elemento cuyo estado se comprobará.
     * @param callback La función de devolución de llamada que se invocará con el estado del elemento.
     */
    private fun checkItemState(entityId: String, callback: (String?) -> Unit) {
        activity.retrofit.create(ApiService::class.java).getItemState(entityId)
            .enqueue(object :
                Callback<ItemStateResponse> {
                override fun onResponse(
                    call: Call<ItemStateResponse>,
                    response: Response<ItemStateResponse>
                ) {
                    if (response.isSuccessful) {
                        val state = response.body()?.state
                        callback(state)
                    } else {
                        Log.e("HomeAssistant", "Error en la conexión: ${response.errorBody()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ItemStateResponse>, t: Throwable) {
                    Log.e("HomeAssistant", "Error en la conexión: ${t.message}")
                    callback(null)
                }
            })
    }
}