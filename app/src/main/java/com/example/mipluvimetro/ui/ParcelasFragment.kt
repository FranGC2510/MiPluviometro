package com.example.mipluvimetro.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.adapter.ParcelaAdapter
import com.example.mipluvimetro.database.ParcelaDAO
import com.example.mipluvimetro.models.Parcela
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento encargado de la gestión de fincas (CRUD).
 * Permite listar, crear y eliminar parcelas de cultivo.
 */
class ParcelasFragment : Fragment() {
    private lateinit var parcelaDAO: ParcelaDAO
    private lateinit var adapter: ParcelaAdapter
    private lateinit var recyclerView: RecyclerView
    private var uriImagenSeleccionada: Uri? = null
    private lateinit var ivImagenDialogo: ImageView

    private val selectorGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriImagenSeleccionada = uri
            ivImagenDialogo.setImageURI(uri)
            ivImagenDialogo.setPadding(0,0,0,0)
            ivImagenDialogo.imageTintList = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parcelas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parcelaDAO = ParcelaDAO(requireContext())
        recyclerView = view.findViewById(R.id.recyclerParcelas)
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fabAddParcela)

        setupRecyclerView()
        cargarParcelas()

        fabAgregar.setOnClickListener {
            mostrarDialogoNuevaParcela()
        }
    }

    /**
     * Configura la lista (RecyclerView) y define la acción de borrado.
     */
    private fun setupRecyclerView() {
        // Al crear el adapter, le pasamos la función de qué hacer cuando se pulsa borrar
        adapter = ParcelaAdapter(emptyList()) { parcelaABorrar ->
            confirmarBorrado(parcelaABorrar)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    /**
     * Consulta la base de datos y actualiza la lista visual.
     */
    private fun cargarParcelas() {
        val lista = parcelaDAO.obtenerTodas()
        adapter.actualizarLista(lista)
    }

    /**
     * Muestra el diálogo flotante para dar de alta una nueva finca.
     */
    private fun mostrarDialogoNuevaParcela() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        // Inflamos nuestro diseño personalizado
        val dialogView = inflater.inflate(R.layout.dialog_nueva_parcela, null)
        builder.setView(dialogView)
        uriImagenSeleccionada = null

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreParcela)
        val etCultivo = dialogView.findViewById<EditText>(R.id.etCultivoParcela)
        val btnGPS = dialogView.findViewById<Button>(R.id.btnUsarGPS)
        val tvCoords = dialogView.findViewById<TextView>(R.id.tvCoordenadasInfo)
        val cardImagen = dialogView.findViewById<View>(R.id.cardImagenParcela)
        ivImagenDialogo = dialogView.findViewById(R.id.ivImagenSeleccionada)

        cardImagen.setOnClickListener {
            // "image/*" significa: déjame elegir cualquier tipo de imagen
            selectorGaleria.launch("image/*")
        }

        // Variables temporales para coordenadas (por defecto 0.0)
        var latTemp = 0.0
        var lonTemp = 0.0

        // TODO: Implementar API de Ubicación real (FusedLocationProvider) más adelante.
        btnGPS.setOnClickListener {
            latTemp = 40.4168
            lonTemp = -3.7038
            tvCoords.text = "Coords: $latTemp, $lonTemp (Simulado)"
            Toast.makeText(requireContext(), getString(R.string.msg_gps_simulado), Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = etNombre.text.toString()
            val cultivo = etCultivo.text.toString()

            if (nombre.isNotEmpty() && cultivo.isNotEmpty()) {
                val nuevaParcela = Parcela(
                    nombre = nombre,
                    cultivo = cultivo,
                    ubicacionLat = latTemp,
                    ubicacionLon = lonTemp,
                    imagenUri = uriImagenSeleccionada?.toString()
                )
                // Guardamos en BD
                parcelaDAO.insertar(nuevaParcela)
                // Refrescamos la lista
                cargarParcelas()
                Toast.makeText(requireContext(), getString(R.string.msg_parcela_guardada), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.err_campos_vacios), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    /**
     * Muestra una alerta de seguridad antes de borrar datos.
     * @param parcela El objeto parcela que el usuario intenta eliminar.
     */
    private fun confirmarBorrado(parcela: Parcela) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_borrar_titulo))
            .setMessage(getString(R.string.dialog_borrar_mensaje, parcela.nombre))
            .setPositiveButton(getString(R.string.btn_borrar)) { _, _ ->
                parcelaDAO.borrar(parcela.id)
                cargarParcelas()
                Toast.makeText(requireContext(), "Parcela eliminada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.btn_cancelar), null)
            .show()
    }
}