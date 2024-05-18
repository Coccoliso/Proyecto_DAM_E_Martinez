package com.emartinez.app_domotica.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.api.ItemStateResponse
import com.emartinez.app_domotica.databinding.ItemHomeassistantBinding

class ItemListViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeassistantBinding.bind(view)
    fun bind(itemStateResponse: ItemStateResponse){
        binding.tvEntityId.text = itemStateResponse.entityId
        binding.tvState.text = itemStateResponse.state
    }
}