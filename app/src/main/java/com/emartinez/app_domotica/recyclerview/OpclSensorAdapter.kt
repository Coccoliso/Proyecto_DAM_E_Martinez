package com.emartinez.app_domotica.recyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R

class OpclSensorAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<OpclSensorViewHolder>() {

    private var openingSensorList: List<ApiItem.OpeningSensor> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(openingSensorList: List<ApiItem.OpeningSensor>) {
        this.openingSensorList = openingSensorList.distinctBy { it.entityId }
        Log.d(
            "OpeningSensorAdapter",
            "Lista de sensores de apertura actualizada con ${this.openingSensorList.size} elementos"
        )
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpclSensorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_opening_sensor, parent, false)
        return OpclSensorViewHolder(view, activity)
    }

    override fun getItemCount(): Int {
        return openingSensorList.size
    }

    override fun onBindViewHolder(holder: OpclSensorViewHolder, position: Int) {
        holder.bind(openingSensorList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        openingSensorList = emptyList()
        notifyDataSetChanged()
    }

}