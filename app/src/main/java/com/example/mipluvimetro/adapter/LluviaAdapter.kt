package com.example.mipluvimetro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.models.Registro

/**
 * Adaptador de RecyclerView encargado de mostrar el historial de lluvias en el Dashboard.
 *
 * Actúa como puente entre los datos (Lista de Registros) y la interfaz visual (XML).
 *
 * @property listaRegistros Lista mutable con los objetos de lluvia a mostrar.
 * @property mapaParcelas Diccionario auxiliar para traducir ID de parcela (Int) a Nombre (String)
 * sin necesidad de consultar la base de datos en cada fila (optimización).
 */
class LluviaAdapter (
    private var listaRegistros: List<Registro>,
    private var mapaParcelas: Map<Int, String>, // ID(1) -> Nombre
    private val onClickEditar: (Registro) -> Unit,
    private val onLongClickBorrar: (Registro) -> Unit
) : RecyclerView.Adapter<LluviaAdapter.LluviaViewHolder>() {

    /**
     * Patrón ViewHolder: Almacena referencias en memoria a los elementos visuales de UNA fila.
     *
     * Importancia:
     * Android recicla las filas que salen de pantalla. Al guardar las referencias aquí,
     * evitamos llamar a `findViewById` cientos de veces al hacer scroll, lo que haría
     * que la lista se sintiera lenta o "trabada".
     */
    class LluviaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLitros: TextView = view.findViewById(R.id.tvLitrosCantidad)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaRegistro)
        val tvParcela: TextView = view.findViewById(R.id.tvNombreParcela)
        val tvIncidencias: TextView = view.findViewById(R.id.tvIncidencias)
        // val ivIcono: ImageView = view.findViewById(R.id.ivIconoLluvia) // Opcional si queremos cambiarlo dinámicamente
    }

    /**
     * Se ejecuta solo cuando el RecyclerView necesita crear una NUEVA fila física en pantalla.
     *
     * @return Un nuevo objeto ViewHolder con el layout `item_registro_lluvia` cargado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LluviaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_lluvia, parent, false)
        return LluviaViewHolder(view)
    }

    /**
     * Paso 2: Vinculación (Relleno de datos).
     * Se ejecuta continuamente mientras el usuario hace scroll.
     * Aquí es donde asignamos los valores de la posición actual a las vistas del ViewHolder.
     *
     * @param holder El contenedor de vistas reciclado o nuevo.
     * @param position La posición del elemento en la lista que queremos mostrar.
     */
    override fun onBindViewHolder(holder: LluviaViewHolder, position: Int) {
        val registro = listaRegistros[position]

        holder.tvLitros.text = registro.litros.toString()
        holder.tvFecha.text = registro.fecha
        holder.tvParcela.text = mapaParcelas[registro.idParcela] ?: "Parcela Desconocida"

        // Lógica de Incidencias (Mostrar u Ocultar)
        if (registro.incidencias.isNotEmpty()) {
            holder.tvIncidencias.text = registro.incidencias
            holder.tvIncidencias.visibility = View.VISIBLE
        } else {
            // Si está vacío, ocultamos el TextView para que no ocupe espacio feo
            holder.tvIncidencias.visibility = View.GONE
        }

        // Click normal -> Editar
        holder.itemView.setOnClickListener {
            onClickEditar(registro)
        }

        // Click largo -> Borrar
        holder.itemView.setOnLongClickListener {
            onLongClickBorrar(registro)
            true // 'true' indica que hemos consumido el evento (para que no salte también el click normal)
        }
    }

    /**
     * Indica al RecyclerView cuántos elementos totales tiene la lista.
     */
    override fun getItemCount(): Int = listaRegistros.size

    /**
     * Método auxiliar para refrescar la lista desde la Actividad/Fragmento.
     *
     * Se usa cuando añadimos una lluvia o cambiamos parcelas. Actualiza las referencias
     * y avisa al adaptador para que repinte la pantalla.
     *
     * @param nuevaLista La lista actualizada de registros de la BD.
     * @param nuevoMapa El mapa actualizado de parcelas.
     */
    fun actualizarDatos(nuevaLista: List<Registro>, nuevoMapa: Map<Int, String>) {
        this.listaRegistros = nuevaLista
        this.mapaParcelas = nuevoMapa
        notifyDataSetChanged() // Refresca la vista
    }
}