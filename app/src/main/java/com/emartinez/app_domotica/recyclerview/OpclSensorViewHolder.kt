package com.emartinez.app_domotica.recyclerview

import android.annotation.SuppressLint
import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.ApiService
import com.emartinez.app_domotica.api.ItemStateResponse
import com.emartinez.app_domotica.databinding.ItemOpeningSensorBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpclSensorViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemOpeningSensorBinding.bind(view)
    private var pollingJob: Job? = null

    fun bind(
        opclSensor: ApiItem.OpeningSensor,
        onItemSelected: (String) -> Unit
    ) {

        startPollingJob(opclSensor)
        Log.d("OpclSensorViewHolder", "openSensor: $opclSensor")
        Log.d("OpeningSensorViewHolder", "Enlazando sensor de apertura: ${opclSensor.entityId}")
        val sensorName = "Sensor " + opclSensor.entityId.split("_").last()
        binding.tvOpeningSensor.text = sensorName

        binding.tvOpeningSensor.setOnClickListener {
            onItemSelected(opclSensor.entityId)
        }
        binding.cvOpeningSensor.setOnClickListener {
           showDialog(opclSensor)
        }

    }

    private fun startPollingJob(opclSensor: ApiItem.OpeningSensor) {
        pollingJob?.cancel()  // Cancela cualquier pollingJob existente antes de iniciar uno nuevo
        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                checkOpeningSensorState(opclSensor)
                delay(5000)  // Espera 1 segundo antes de la próxima comprobación
            }
        }
    }

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

    fun unbind() {
        pollingJob?.cancel()  // Cancela el pollingJob cuando el ViewHolder ya no está enlazado
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog(opclSensor: ApiItem.OpeningSensor) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_opcl_sensor)

        val sensorName = "Sensor " + opclSensor.entityId.split("_").last()
        val sensorBatteryName = "sensor." + opclSensor.entityId.split(".").last() + "_battery"
        val sensorTemperatureName = "sensor." + opclSensor.entityId.split(".").last() + "_temperature"

        Log.i("OpeningSensorViewHolder", "Mostrando diálogo para el sensor de apertura $sensorName")
        Log.i("OpeningSensorViewHolder", "Sensor de batería: $sensorBatteryName")
        Log.i("OpeningSensorViewHolder", "Sensor de temperatura: $sensorTemperatureName")

        val btnReturn: Button = dialog.findViewById(R.id.btnReturn)
        val tvSensorNameDialog: TextView = dialog.findViewById(R.id.tvSensorNameDialog)
        val tvSensorBattery: TextView = dialog.findViewById(R.id.tvSensorBattery)
        val tvSensorTemperature: TextView = dialog.findViewById(R.id.tvSensorTemperature)

        checkItemState(sensorBatteryName) { batteryState ->
            tvSensorBattery.text = "Batería: $batteryState"
        }
        checkItemState(sensorTemperatureName) { temperatureState ->
            tvSensorTemperature.text = "Temperatura: $temperatureState"
        }

        tvSensorNameDialog.text = sensorName


        btnReturn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkItemState(EntityId: String, callback: (String?) -> Unit) {
        activity.retrofit.create(ApiService::class.java).getItemState(EntityId)
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