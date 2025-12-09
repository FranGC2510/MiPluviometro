package com.example.mipluvimetro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.models.Parcela

/**
 * Adaptador para el RecyclerView que gestiona la lista de Fincas (Parcelas).
 *
 * Responsabilidad:
 * Convertir los objetos [Parcela] en elementos visuales (tarjetas) para la lista.
 *
 * @property listaParcelas Lista mutable de datos que se mostrarán.
 * @property onBorrarClick Función Lambda (Callback). Recibe una [Parcela] y no devuelve nada (Unit).
 * Se usa para avisar al Fragmento de que el usuario ha pulsado el botón "papelera".
 * De esta forma, la lógica de borrar (Diálogos, BD) se mantiene fuera del adaptador.
 */
class ParcelaAdapter (
    private var listaParcelas: List<Parcela>,
    private val onBorrarClick: (Parcela) -> Unit
) : RecyclerView.Adapter<ParcelaAdapter.ParcelaViewHolder>() {
    /**
     * ViewHolder: Caché de vistas.
     * Mantiene las referencias a los elementos visuales de una fila (CardView)
     * para no tener que buscarlos con findViewById cada vez que se hace scroll.
     */
    class ParcelaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreFinca)
        val tvCultivo: TextView = view.findViewById(R.id.tvCultivo)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrarParcela)
    }

    /**
     * Crea una nueva "caja" vacía (ViewHolder) basada en el diseño XML 'item_parcela'.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parcela, parent, false)
        return ParcelaViewHolder(view)
    }

    /**
     * Rellena la "caja" con los datos de la parcela en la posición actual.
     */
    override fun onBindViewHolder(holder: ParcelaViewHolder, position: Int) {
        val parcela = listaParcelas[position]

        holder.tvNombre.text = parcela.nombre
        holder.tvCultivo.text = "Cultivo: ${parcela.cultivo}"

        // Configurar el click de la papelera
        holder.btnBorrar.setOnClickListener {
            onBorrarClick(parcela) // Ejecutamos la acción que nos pasó el Fragmento
        }
    }

    /**
     * Indica el tamaño total de la lista al sistema.
     */
    override fun getItemCount(): Int = listaParcelas.size

    /**
     * Método auxiliar para refrescar la lista completa cuando hay cambios en la base de datos
     * (añadir o borrar parcelas).
     */
    fun actualizarLista(nuevaLista: List<Parcela>) {
        this.listaParcelas = nuevaLista
        notifyDataSetChanged()
    }
}