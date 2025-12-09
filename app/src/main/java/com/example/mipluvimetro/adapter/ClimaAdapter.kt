package com.example.mipluvimetro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.network.Prevision
import java.text.SimpleDateFormat
import java.util.Locale

class ClimaAdapter (
    private var listaPrevision: List<Prevision>
) : RecyclerView.Adapter<ClimaAdapter.ClimaViewHolder>(){
    class ClimaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvLluvia: TextView = view.findViewById(R.id.tvLluviaPrevista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClimaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pronostico, parent, false)
        return ClimaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClimaViewHolder, position: Int) {
        val item = listaPrevision[position]

        // Formateo de Fecha y Hora
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(item.fechaHora)

            if (date != null) {
                // Formato Hora: "15:00"
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
                holder.tvHora.text = formatoHora.format(date)

                // Formato Fecha: "Sáb, 9 Dic"
                val formatoFecha = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
                holder.tvFecha.text = formatoFecha.format(date).replaceFirstChar { it.uppercase() }
            }
        } catch (e: Exception) {
            holder.tvHora.text = item.fechaHora
        }

        // Temperatura con 1 decimal
        holder.tvTemp.text = String.format("%.1f°C", item.main.temp)

        // Descripción
        val desc = item.weather.firstOrNull()?.description ?: ""
        holder.tvDescripcion.text = desc.replaceFirstChar { it.uppercase() }

        // El objeto 'rain' puede ser nulo si no hay previsión de lluvia
        val litros = item.rain?.litros3h ?: 0.0

        if (litros > 0) {
            holder.tvLluvia.visibility = View.VISIBLE
            holder.tvLluvia.text = String.format("%.1f mm", litros)
        } else {
            holder.tvLluvia.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = listaPrevision.size

    fun actualizarLista(nuevaLista: List<Prevision>) {
        this.listaPrevision = nuevaLista
        notifyDataSetChanged()
    }
}