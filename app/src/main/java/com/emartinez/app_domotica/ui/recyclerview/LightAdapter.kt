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
 * Clase `LightAdapter` que se encarga de proporcionar las vistas para los elementos de la lista de luces en la interfaz de usuario.
 *
 * @property activity La actividad en la que se utiliza este adaptador.
 * @property lightList La lista de luces que se mostrarán en la interfaz de usuario.
 */
class LightAdapter(private val activity: HomeAssistantActivity) :
    RecyclerView.Adapter<LightViewHolder>() {

    private var lightList: List<ApiItem.Light> = emptyList()

    /**
     * Actualiza la lista de luces que se mostrarán en la interfaz de usuario.
     *
     * @param lightList La nueva lista de luces.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(lightList: List<ApiItem.Light>) {
        this.lightList = lightList
        Log.d("LightAdapter", "Lista de luces actualizada con ${lightList.size} elementos")
        notifyDataSetChanged()
    }

    /**
     * Crea una nueva vista para un elemento de la lista de luces.
     *
     * @param parent El ViewGroup al que se añadirá la nueva vista después de que se enlace a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista para el elemento de la lista de luces.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_light, parent, false)
        return LightViewHolder(view, activity)
    }

    /**
     * Devuelve el número total de elementos en la lista de luces.
     *
     * @return El número total de elementos en la lista de luces.
     */
    override fun getItemCount(): Int {
        return lightList.size
    }

    /**
     * Actualiza el contenido de la vista para un elemento de la lista de luces en una posición específica.
     *
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento en el conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: LightViewHolder, position: Int) {
        holder.bind(lightList[position])
    }

    /**
     * Limpia la lista de luces.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        lightList = emptyList()
        notifyDataSetChanged()
    }
}