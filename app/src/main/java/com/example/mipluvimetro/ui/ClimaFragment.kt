package com.example.mipluvimetro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.adapter.ClimaAdapter
import com.example.mipluvimetro.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ClimaFragment : Fragment() {
    private val API_KEY = "44513f74a1a84071cd96c2f96918e30b"

    private lateinit var adapter: ClimaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var etCiudad: TextInputEditText
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clima, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCiudad = view.findViewById(R.id.etCiudad)
        progressBar = view.findViewById(R.id.progressBarClima)
        recycler = view.findViewById(R.id.recyclerClima)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)

        setupRecyclerView()

        btnBuscar.setOnClickListener {
            val ciudad = etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                ocultarTeclado()
                buscarClima(ciudad)
            } else {
                Toast.makeText(requireContext(), "Escribe una ciudad", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ClimaAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
    }

    /**
     * Lógica de Red (Networking).
     * Usamos lifecycleScope.launch para no bloquear la App mientras descarga.
     */
    private fun buscarClima(nombreCiudad: String) {
        progressBar.visibility = View.VISIBLE
        recycler.visibility = View.GONE

        //Lanzar petición en segundo plano
        lifecycleScope.launch {
            try {
                // LLAMADA A LA API
                val respuesta = RetrofitClient.servicio.obtenerPronosticoPorCiudad(
                    ciudad = nombreCiudad,
                    apiKey = API_KEY
                )

                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()
                    val listaPrevision = datos?.list ?: emptyList()

                    if (listaPrevision.isNotEmpty()) {
                        adapter.actualizarLista(listaPrevision)
                        recycler.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), "No hay datos disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: Ciudad no encontrada", Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Sin conexión a Internet", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun ocultarTeclado() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}