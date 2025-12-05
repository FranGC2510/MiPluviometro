package com.example.mipluvimetro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.adapter.LluviaAdapter
import com.example.mipluvimetro.database.ParcelaDAO
import com.example.mipluvimetro.database.RegistroDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragmento principal (Dashboard) de la aplicación.
 *
 * Responsabilidades:
 * 1. Mostrar un resumen estadístico (Totales de lluvia del mes y del año).
 * 2. Listar los últimos registros de precipitaciones.
 * 3. Actualizar la información cada vez que la pantalla se hace visible.
 */
class HomeFragment : Fragment() {
    private lateinit var registroDAO: RegistroDAO
    private lateinit var parcelaDAO: ParcelaDAO
    private lateinit var adapter: LluviaAdapter

    private lateinit var tvTotalMes: TextView
    private lateinit var tvTotalAnio: TextView
    private lateinit var recyclerView: RecyclerView

    /**
     * Convierte el archivo XML (fragment_home.xml) en objetos visuales reales.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Se ejecuta cuando la vista ya existe. Aquí conectamos la lógica con la interfaz.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        registroDAO = RegistroDAO(context)
        parcelaDAO = ParcelaDAO(context)

        tvTotalMes = view.findViewById(R.id.tvTotalMes)
        tvTotalAnio = view.findViewById(R.id.tvTotalAnio)
        recyclerView = view.findViewById(R.id.recyclerViewLluvias)

        setupRecyclerView()
    }

    /**
     * Carga de Datos.
     * Usamos 'onResume' en lugar de 'onCreate' para garantizar que los datos se refresquen
     * si el usuario vuelve a esta pantalla después de añadir un registro nuevo.
     */
    override fun onResume() {
        super.onResume()
        cargarDatosDashboard()
    }

    /**
     * Configura el motor del RecyclerView (Lista).
     * Define cómo se colocan los elementos (LinearLayout = uno debajo de otro).
     */
    private fun setupRecyclerView() {
        adapter = LluviaAdapter(emptyList(), emptyMap())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    /**
     * Orquesta la obtención de datos de la BD y actualiza la pantalla.
     */
    private fun cargarDatosDashboard() {
        // Obtenemos los últimos 20 registros
        val ultimosRegistros = registroDAO.obtenerUltimos(20)

        // Obtenemos TODAS las parcelas y las convertimos a un Mapa (ID -> Nombre)
        val mapaParcelas = parcelaDAO.obtenerTodas().associate { it.id to it.nombre }

        adapter.actualizarDatos(ultimosRegistros, mapaParcelas)

        calcularTotales()
    }

    /**
     * Calcula la suma de litros del mes actual y del año en curso.
     */
    private fun calcularTotales() {
        val calendar = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val hoy = formatoFecha.format(calendar.time)

        // Total Mes Actual (Desde el día 1 del mes presente)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val inicioMes = formatoFecha.format(calendar.time)

        val litrosMes = registroDAO.sumarLitrosPorRango(inicioMes, hoy)
        tvTotalMes.text = String.format("%.1f", litrosMes) // Ej: "15.5"

        // Total Año Natural (Desde el 1 de Enero del año presente)
        val calAnio = Calendar.getInstance() // Nueva instancia para no mezclar
        calAnio.set(Calendar.DAY_OF_MONTH, 1)
        calAnio.set(Calendar.MONTH, Calendar.JANUARY) // Enero (Mes 0)

        val inicioAnio = formatoFecha.format(calAnio.time)

        val litrosAnio = registroDAO.sumarLitrosPorRango(inicioAnio, hoy)
        tvTotalAnio.text = String.format("%.1f", litrosAnio)
    }
}