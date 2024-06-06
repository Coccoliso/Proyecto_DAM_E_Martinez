package com.emartinez.app_domotica.ui.recyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.model.ApiItem

/**
 * Clase `CameraAdapter` que se encarga de proporcionar el adaptador para la lista de cámaras en la interfaz de usuario.
 *
 * @property activity La actividad en la que se utiliza este adaptador.
 */
class CameraAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<CameraViewHolder>() {


    private var cameraList: List<ApiItem.Camera> = emptyList()

    /**
     * Actualiza la lista de cámaras que se mostrará en la interfaz de usuario.
     *
     * @param cameraList La nueva lista de cámaras.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(cameraList: List<ApiItem.Camera>) {
        this.cameraList = cameraList
        Log.d("CameraAdapter", "Lista de camaras actualizada con ${cameraList.size} elementos")
        notifyDataSetChanged()
    }

    /**
     * Crea un nuevo ViewHolder para la lista de cámaras.
     *
     * @param parent El ViewGroup en el que se añadirá el nuevo ViewHolder.
     * @param viewType El tipo de vista del nuevo ViewHolder.
     * @return Un nuevo ViewHolder para la lista de cámaras.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
        return CameraViewHolder(view, activity)
    }

    /**
     * Devuelve el número de elementos en la lista de cámaras.
     *
     * @return El número de elementos en la lista de cámaras.
     */
    override fun getItemCount(): Int {
        return cameraList.size
    }

    /**
     * Enlaza los datos de una cámara con un ViewHolder.
     *
     * @param holder El ViewHolder que se enlazará con los datos de una cámara.
     * @param position La posición de la cámara en la lista.
     */
    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(cameraList[position])
    }

    /**
     * Limpia la lista de cámaras.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        cameraList = emptyList()
        notifyDataSetChanged()
    }
}