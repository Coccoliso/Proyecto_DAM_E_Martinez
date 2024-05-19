package com.emartinez.app_domotica.recyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.databinding.ItemOpeningSensorBinding

class OpeningSensorViewHolder(view: View, private val activity: HomeAssistantActivity): RecyclerView.ViewHolder(view) {

    private val binding = ItemOpeningSensorBinding.bind(view)
    fun bind(item: ApiItem.OpeningSensor){
        Log.d("OpeningSensorViewHolder", "Enlazando sensor de apertura: ${item.entityId}")

    }
}