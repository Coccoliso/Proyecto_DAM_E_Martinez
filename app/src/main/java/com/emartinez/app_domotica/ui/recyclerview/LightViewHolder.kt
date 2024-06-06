package com.emartinez.app_domotica.ui.recyclerview

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.model.ApiService
import com.emartinez.app_domotica.model.ChangeBrightnessBody
import com.emartinez.app_domotica.model.ChangeColorBody
import com.emartinez.app_domotica.model.EntityId
import com.emartinez.app_domotica.databinding.ItemLightBinding
import com.emartinez.app_domotica.model.ApiItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.flask.colorpicker.ColorPickerView


class LightViewHolder(view: View, private val activity: HomeAssistantActivity) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemLightBinding.bind(view)

    fun bind(light: ApiItem.Light) {
        Log.d("LightViewHolder", "Enlazando luz: ${light.entityId}")

        val lightName = light.entityId.split(".").last().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        binding.tvLightId.text = lightName
        checkLightState(light)

        binding.cvLight.setOnClickListener {
            showDialog(light)
        }

        binding.swLight.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.Main).launch {
               changeLightState(light.entityId, isChecked)
            }
        }
    }

    private fun checkLightState(item: ApiItem.Light) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                activity.retrofit.create(ApiService::class.java).getLightState(item.entityId)
                    .execute()
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    val response = myResponse.body()
                    val state = response?.state
                    Log.d("LightViewHolder", "Estado de la luz ${item.entityId}: $state")
                    binding.swLight.isChecked = state == "on"
                    if (state == "on") {
                        binding.ivLight.setImageResource(R.drawable.ic_light_on)
                    } else {
                        binding.ivLight.setImageResource(R.drawable.ic_light_off)
                    }
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                }
            }
        }
    }

    private suspend fun changeLightState(entityId: String, state: Boolean) {
        return CoroutineScope(Dispatchers.IO).async {
            if (state) {
                activity.retrofit.create(ApiService::class.java)
                    .turnOnLight(EntityId(entityId)).execute()
                withContext(Dispatchers.Main) {
                    binding.ivLight.setImageResource(R.drawable.ic_light_on)
                }
            } else {
                activity.retrofit.create(ApiService::class.java)
                    .turnOffLight(EntityId(entityId)).execute()
                withContext(Dispatchers.Main) {
                    binding.ivLight.setImageResource(R.drawable.ic_light_off)
                }
            }
        }.await()
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog(light: ApiItem.Light) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_light)

        val lightName = light.entityId.split(".").last().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val tvLightName: TextView = dialog.findViewById(R.id.tvLightNameDialog)
        tvLightName.text = lightName

        val btnReturn: Button = dialog.findViewById(R.id.btnReturn)
        btnReturn.setOnClickListener {
            dialog.dismiss()
        }
        updateDialogValues(dialog, light)

        val brightnessSeekBar = dialog.findViewById<SeekBar>(R.id.sbBrightness)
        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val changeBrightnessBody = ChangeBrightnessBody(light.entityId, progress)
                        activity.retrofit.create(ApiService::class.java)
                            .changeLightBrightness(changeBrightnessBody).execute()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val colorPickerView = dialog.findViewById<ColorPickerView>(R.id.color_picker_view)

        colorPickerView.addOnColorChangedListener { selectedColor ->
            val red = Color.red(selectedColor)
            val green = Color.green(selectedColor)
            val blue = Color.blue(selectedColor)
            val rgbColor = listOf(red, green, blue)
            CoroutineScope(Dispatchers.IO).launch {
                val changeColorBody = ChangeColorBody(light.entityId, rgbColor)
                val myResponse = activity.retrofit.create(ApiService::class.java)
                    .changeLightColor(changeColorBody).execute()
                withContext(Dispatchers.Main) {
                    if (myResponse.isSuccessful) {
                        Log.d("LightViewHolder", "Color cambiado con éxito")
                    } else {
                        Log.e("HomeAssistant", "Error al cambiar el color: ${myResponse.errorBody()}")
                    }
                }
            }
        }
        dialog.show()
    }

    private fun updateDialogValues(dialog: Dialog, light: ApiItem.Light) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse =
                activity.retrofit.create(ApiService::class.java).getLightState(light.entityId)
                    .execute()
            withContext(Dispatchers.Main) {
                if (myResponse.isSuccessful) {
                    val response = myResponse.body()
                    val brightness = response?.attributes?.brightness ?: 0
                    val rgbColor =
                        response?.attributes?.rgbColor ?: listOf(0, 0, 0)
                    val color = Color.rgb(rgbColor[0], rgbColor[1], rgbColor[2])

                    Log.d("LightViewHolder", "Brillo: $brightness, color: $color")
                    dialog.findViewById<SeekBar>(R.id.sbBrightness).progress = brightness
                    dialog.findViewById<ColorPickerView>(R.id.color_picker_view)
                        .setColor(color, true)
                } else {
                    Log.e("HomeAssistant", "Error en la conexión: ${myResponse.errorBody()}")
                }
            }
        }
    }

}