package com.emartinez.app_domotica.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R

class OpeningSensorAdapter (private val activity: HomeAssistantActivity) :
        RecyclerView.Adapter<OpeningSensorViewHolder>() {

        private var openingSensorList: List<ApiItem.OpeningSensor> = emptyList()

        fun updateList(openingSensorList: List<ApiItem.OpeningSensor>) {
            this.openingSensorList = openingSensorList
            Log.d("OpeningSensorAdapter", "Lista de sensores de apertura actualizada con ${openingSensorList.size} elementos")
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpeningSensorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_opening_sensor, parent, false)
            return OpeningSensorViewHolder(view, activity)
        }

        override fun getItemCount(): Int {
            return openingSensorList.size
        }

        override fun onBindViewHolder(holder: OpeningSensorViewHolder, position: Int) {
            holder.bind(openingSensorList[position])
        }

}