package com.emartinez.app_domotica.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.ItemStateResponse

class HomeAssistantAdapter(var itemList: List<ItemStateResponse> = emptyList()) :
    RecyclerView.Adapter<ItemListViewHolder>() {

        @SuppressLint("NotifyDataSetChanged")
        fun updateList(itemList: List<ItemStateResponse>){
            this.itemList = itemList
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_homeassistant, parent, false)
        )
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(viewHolder: ItemListViewHolder, position: Int) {

        viewHolder.bind(itemList[position])
    }

}