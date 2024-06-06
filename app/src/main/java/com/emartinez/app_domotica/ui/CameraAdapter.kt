package com.emartinez.app_domotica.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.model.ApiItem

class CameraAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<CameraViewHolder>() {


    private var cameraList: List<ApiItem.Camera> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(cameraList: List<ApiItem.Camera>) {
        this.cameraList = cameraList
        Log.d("LightAdapter", "Lista de luces actualizada con ${cameraList.size} elementos")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
        return CameraViewHolder(view, activity)
    }

    override fun getItemCount(): Int {
        return cameraList.size
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(cameraList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        cameraList = emptyList()
        notifyDataSetChanged()
    }
}