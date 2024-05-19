package com.emartinez.app_domotica.recyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.ItemStateResponse

class HomeAssistantAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: List<ApiItem> = emptyList()
    companion object {
        const val TYPE_LIGHT = 0
        const val TYPE_OPENING_SENSOR = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(itemList: List<ApiItem>) {
        this.itemList = itemList
        Log.d("HomeAssistantAdapter", "Lista actualizada, notificando cambios")
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val viewType = when (itemList[position]) {
            is ApiItem.Light -> TYPE_LIGHT
            is ApiItem.OpeningSensor -> TYPE_OPENING_SENSOR
        }
        Log.d("HomeAssistantAdapter", "getItemViewType() para posición $position devolvió $viewType")
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LIGHT -> {
                Log.d("HomeAssistantAdapter", "Creando LightViewHolder")
                LightViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_light, parent, false),
                    activity
                )
            }

            TYPE_OPENING_SENSOR -> {
                Log.d("HomeAssistantAdapter", "Creando OpeningSensorViewHolder")
                OpeningSensorViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_opening_sensor, parent, false), activity
                )
            }

            else -> {
                Log.e("HomeAssistantAdapter", "Tipo de vista inválido: $viewType")
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemCount(): Int {
        val count = itemList.size
        Log.d("HomeAssistantAdapter", "getItemCount() devolvió $count")
        return count
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("HomeAssistantAdapter", "onBindViewHolder() para posición $position")
        when (val item = itemList[position]) {
            is ApiItem.Light -> (holder as LightViewHolder).bind(item)
            is ApiItem.OpeningSensor -> (holder as OpeningSensorViewHolder).bind(item)
        }
    }
}