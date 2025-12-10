package com.example.mipluvimetro.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mipluvimetro.R
import com.example.mipluvimetro.adapter.LluviaAdapter
import com.example.mipluvimetro.database.ParcelaDAO
import com.example.mipluvimetro.database.RegistroDAO
import com.example.mipluvimetro.models.Registro
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    private val sdfBd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Para SQLite
    private val sdfVisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Para el usuario

    private lateinit var barChart: BarChart

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
        barChart = view.findViewById(R.id.graficaLluvia)
        recyclerView = view.findViewById(R.id.recyclerViewLluvias)

        view.findViewById<FloatingActionButton>(R.id.fabAddLluvia).setOnClickListener {
            gestionarDialogoRegistro(null)
        }

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
        adapter = LluviaAdapter(
            emptyList(),
            emptyMap(),
            onClickEditar = { registro -> gestionarDialogoRegistro(registro) },
            onLongClickBorrar = { registro -> confirmarBorradoRegistro(registro) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    /**
     * Orquesta la obtención de datos de la BD y actualiza la pantalla.
     */
    private fun cargarDatosDashboard() {
        val ultimosRegistros = registroDAO.obtenerUltimos(50)

        // Obtenemos TODAS las parcelas y las convertimos a un Mapa (ID -> Nombre)
        val mapaParcelas = parcelaDAO.obtenerTodasIncluidoBorradas().associate { it.id to it.nombre }

        adapter.actualizarDatos(ultimosRegistros, mapaParcelas)
        calcularTotales()
        cargarGrafica(ultimosRegistros)
    }

    private fun cargarGrafica(registros: List<Registro>) {
        // Usamos TreeMap para que se ordenen automáticamente por fecha (clave)
        val mapaMensual = java.util.TreeMap<String, Float>()

        val formatoClave = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val formatoEtiqueta = SimpleDateFormat("MMM", Locale.getDefault())

        registros.forEach { registro ->
            try {
                val fecha = sdfBd.parse(registro.fecha)
                if (fecha != null) {
                    val clave = formatoClave.format(fecha)
                    val litrosActuales = mapaMensual[clave] ?: 0f
                    mapaMensual[clave] = litrosActuales + registro.litros
                }
            } catch (e: Exception) {
                // Ignorar fechas mal formadas
            }
        }

        val entradas = ArrayList<BarEntry>()
        val etiquetas = ArrayList<String>()
        var indice = 0f

        mapaMensual.forEach { (claveAñoMes, litrosTotal) ->
            entradas.add(BarEntry(indice, litrosTotal))

            val fechaObj = formatoClave.parse(claveAñoMes)
            val etiquetaBonita = formatoEtiqueta.format(fechaObj!!).replaceFirstChar { it.uppercase() }
            etiquetas.add(etiquetaBonita)

            indice++
        }

        val dataSet = BarDataSet(entradas, "Litros Mensuales")
        dataSet.color = requireContext().getColor(R.color.primary_blue)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        data.barWidth = 0.6f

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.animateY(1500)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(etiquetas)
        xAxis.granularity = 1f
        xAxis.labelCount = etiquetas.size

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f

        barChart.extraBottomOffset = 10f

        barChart.invalidate()
    }

    /**
     * Calcula la suma de litros del mes actual y del año en curso.
     */
    private fun calcularTotales() {
        val calendar = Calendar.getInstance()
        val hoy = sdfBd.format(calendar.time)

        // Total Mes Actual (Desde el día 1 del mes presente)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val inicioMes = sdfBd.format(calendar.time)
        val litrosMes = registroDAO.sumarLitrosPorRango(inicioMes, hoy)
        tvTotalMes.text = String.format(Locale.getDefault(), "%.1f", litrosMes)

        // Total Año Natural (Desde el 1 de Enero del año presente)
        val calAnio = Calendar.getInstance()
        calAnio.set(Calendar.DAY_OF_MONTH, 1)
        calAnio.set(Calendar.MONTH, Calendar.JANUARY)

        val inicioAnio = sdfBd.format(calAnio.time)
        val litrosAnio = registroDAO.sumarLitrosPorRango(inicioAnio, hoy)
        tvTotalAnio.text = String.format(Locale.getDefault(), "%.1f", litrosAnio)
    }

    /**
     * Función UNIFICADA para Crear o Editar un registro.
     * @param registroEditar Si es null, crea uno nuevo. Si existe, edita ese objeto.
     */
    private fun gestionarDialogoRegistro(registroEditar: Registro?) {
        val esEdicion = registroEditar != null

        // Si editamos, traemos TAMBIÉN las borradas (para no romper registros antiguos).
        // Si es nuevo, solo traemos las activas.
        val listaParcelas = if (esEdicion) parcelaDAO.obtenerTodasIncluidoBorradas() else parcelaDAO.obtenerTodas()

        if (listaParcelas.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_sin_parcelas), Toast.LENGTH_LONG).show()
            return
        }

        // Preparar el Diálogo
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_registro, null)
        builder.setView(dialogView)

        // Referencias a UI
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloRegistro)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerParcelas)
        val etLitros = dialogView.findViewById<TextInputEditText>(R.id.etLitros)
        val tvFecha = dialogView.findViewById<TextView>(R.id.tvFechaSeleccionada)
        val switchIncidencias = dialogView.findViewById<SwitchMaterial>(R.id.switchIncidencias)
        val layoutIncidencias = dialogView.findViewById<TextInputLayout>(R.id.layoutIncidencias)
        val etIncidencias = dialogView.findViewById<TextInputEditText>(R.id.etIncidencias)
        val cbEmail = dialogView.findViewById<CheckBox>(R.id.cbEnviarReporte)

        dialogView.findViewById<TextView>(R.id.tvTituloRegistro)?.text =
            getString(if (esEdicion) R.string.titulo_editar_registro else R.string.titulo_nuevo_registro)

        // Ocultar checkbox de email si editamos
        if (esEdicion) cbEmail.visibility = View.GONE

        val nombresParcelas = listaParcelas.map { it.nombre }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresParcelas)
        spinner.adapter = spinnerAdapter

        var fechaGuardar = sdfBd.format(Date())

        // Pre-llenado de datos
        if (esEdicion && registroEditar != null) {
            etLitros.setText(registroEditar.litros.toString())
            fechaGuardar = registroEditar.fecha

            // Buscar posición en el spinner
            val indice = listaParcelas.indexOfFirst { it.id == registroEditar.idParcela }
            if (indice >= 0) spinner.setSelection(indice)

            if (registroEditar.incidencias.isNotEmpty()) {
                switchIncidencias.isChecked = true
                etIncidencias.setText(registroEditar.incidencias)
                layoutIncidencias.visibility = View.VISIBLE
            }
        }

        actualizarTextoFecha(tvFecha, fechaGuardar)

        tvFecha.setOnClickListener {
            mostrarSelectorFecha(fechaGuardar) { nuevaFechaBd ->
                fechaGuardar = nuevaFechaBd
                actualizarTextoFecha(tvFecha, fechaGuardar)
            }
        }

        switchIncidencias.setOnCheckedChangeListener { _, isChecked ->
            layoutIncidencias.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Botón Guardar / Actualizar
        val textoBoton = if (esEdicion) getString(R.string.btn_actualizar) else getString(R.string.btn_guardar)
        builder.setPositiveButton(textoBoton) { dialog, _ ->
            val litrosTexto = etLitros.text.toString()

            if (litrosTexto.isNotEmpty()) {
                val parcela = listaParcelas[spinner.selectedItemPosition]
                val incidencias = if (switchIncidencias.isChecked) etIncidencias.text.toString() else ""
                val litros = litrosTexto.toFloatOrNull() ?: 0f

                if (esEdicion && registroEditar != null) {
                    val registroActualizado = registroEditar.copy(
                        idParcela = parcela.id,
                        fecha = fechaGuardar,
                        litros = litros,
                        incidencias = incidencias
                    )
                    registroDAO.actualizar(registroActualizado)
                    Toast.makeText(requireContext(), getString(R.string.msg_registro_actualizado), Toast.LENGTH_SHORT).show()
                } else {
                    val nuevoRegistro = Registro(
                        idParcela = parcela.id,
                        fecha = fechaGuardar,
                        litros = litros,
                        incidencias = incidencias
                    )
                    registroDAO.insertar(nuevoRegistro)
                    Toast.makeText(requireContext(), getString(R.string.msg_parcela_guardada), Toast.LENGTH_SHORT).show()

                    if (cbEmail.isChecked) {
                        Toast.makeText(requireContext(), "Reporte enviado (Simulado)", Toast.LENGTH_SHORT).show()
                    }
                }

                cargarDatosDashboard()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.msg_falta_litros), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.btn_cancelar), null)
        builder.create().show()
    }

    /**
     * Muestra alerta de confirmación antes de borrar.
     */
    private fun confirmarBorradoRegistro(registro: Registro) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_eliminar_registro_titulo))
            .setMessage(getString(R.string.dialog_eliminar_registro_msg, registro.litros, registro.fecha))
            .setPositiveButton(getString(R.string.btn_borrar)) { _, _ ->
                registroDAO.borrar(registro.id)
                cargarDatosDashboard()
                Toast.makeText(requireContext(), getString(R.string.msg_registro_eliminado), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.btn_cancelar), null)
            .show()
    }

    private fun mostrarSelectorFecha(fechaActualBd: String, alSeleccionar: (String) -> Unit) {
        val cal = Calendar.getInstance()
        try {
            cal.time = sdfBd.parse(fechaActualBd)!!
        } catch (e: Exception) { /* Ignorar error de parseo */ }

        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)
            alSeleccionar(sdfBd.format(cal.time))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun actualizarTextoFecha(textView: TextView, fechaBd: String) {
        try {
            val date = sdfBd.parse(fechaBd)
            textView.text = String.format(getString(R.string.formato_fecha_visible), sdfVisible.format(date!!))
        } catch (e: Exception) {
            textView.text = fechaBd
        }
    }
}