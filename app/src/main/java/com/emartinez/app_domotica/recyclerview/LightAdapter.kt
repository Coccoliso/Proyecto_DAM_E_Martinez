package com.emartinez.app_domotica.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R

class LightAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<LightViewHolder>() {

    private var lightList: List<ApiItem.Light> = emptyList()

    fun updateList(lightList: List<ApiItem.Light>) {
        this.lightList = lightList
        Log.d("LightAdapter", "Lista de luces actualizada con ${lightList.size} elementos")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_light, parent, false)
        return LightViewHolder(view, activity)
    }

    override fun getItemCount(): Int {
        return lightList.size
    }

    override fun onBindViewHolder(holder: LightViewHolder, position: Int) {
        holder.bind(lightList[position])
    }
}