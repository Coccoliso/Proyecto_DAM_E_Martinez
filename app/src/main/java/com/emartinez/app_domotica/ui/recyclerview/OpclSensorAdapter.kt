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
 * Clase `OpclSensorAdapter` que se encarga de proporcionar el adaptador para la lista de sensores de apertura en la interfaz de usuario.
 *
 * @property activity La actividad en la que se utiliza este adaptador.
 */
class OpclSensorAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<OpclSensorViewHolder>() {

    private var openingSensorList: List<ApiItem.OpeningSensor> = emptyList()

    /**
     * Actualiza la lista de sensores de apertura que se mostrará en la interfaz de usuario.
     *
     * @param openingSensorList La nueva lista de sensores de apertura.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(openingSensorList: List<ApiItem.OpeningSensor>) {
        this.openingSensorList = openingSensorList.distinctBy { it.entityId }
        Log.d(
            "OpeningSensorAdapter",
            "Lista de sensores de apertura actualizada con ${this.openingSensorList.size} elementos"
        )
        notifyDataSetChanged()
    }

    /**
     * Crea un nuevo ViewHolder para la lista de sensores de apertura.
     *
     * @param parent El ViewGroup en el que se añadirá el nuevo ViewHolder.
     * @param viewType El tipo de vista del nuevo ViewHolder.
     * @return Un nuevo ViewHolder para la lista de sensores de apertura.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpclSensorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_opening_sensor, parent, false)
        return OpclSensorViewHolder(view, activity)
    }

    /**
     * Devuelve el número de elementos en la lista de sensores de apertura.
     *
     * @return El número de elementos en la lista de sensores de apertura.
     */
    override fun getItemCount(): Int {
        return openingSensorList.size
    }

    /**
     * Enlaza los datos de un sensor de apertura con un ViewHolder.
     *
     * @param holder El ViewHolder que se enlazará con los datos de un sensor de apertura.
     * @param position La posición del sensor de apertura en la lista.
     */
    override fun onBindViewHolder(holder: OpclSensorViewHolder, position: Int) {
        holder.bind(openingSensorList[position])
    }

    /**
     * Limpia la lista de sensores de apertura.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        openingSensorList = emptyList()
        notifyDataSetChanged()
    }

}