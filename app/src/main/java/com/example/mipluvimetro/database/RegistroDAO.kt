package com.example.mipluvimetro.database

import android.content.ContentValues
import android.content.Context
import com.example.mipluvimetro.models.Registro

/**
 * Data Access Object (DAO) para la entidad [Registro].
 *
 * Gestiona la inserción de lluvias y consultas complejas como:
 * - Obtener el historial reciente.
 * - Calcular sumas totales (Estadísticas) por rangos de fecha.
 */
class RegistroDAO(context: Context) {
    private val dbHelper = AdminSQLite(context)

    fun insertar(registro: Registro): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AdminSQLite.COL_REG_PARCELA_ID, registro.idParcela)
            put(AdminSQLite.COL_REG_FECHA, registro.fecha)
            put(AdminSQLite.COL_REG_LITROS, registro.litros)
            put(AdminSQLite.COL_REG_INCIDENCIAS, registro.incidencias)
        }
        val id = db.insert(AdminSQLite.TABLA_REGISTROS, null, values)
        db.close()
        return id
    }

    fun obtenerUltimos(cantidad: Int = 20): List<Registro> {
        val lista = ArrayList<Registro>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${AdminSQLite.TABLA_REGISTROS} ORDER BY ${AdminSQLite.COL_REG_FECHA} DESC LIMIT ?"
        val cursor = db.rawQuery(query, arrayOf(cantidad.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLite.COL_REG_ID))
                val idParcela = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLite.COL_REG_PARCELA_ID))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_REG_FECHA))
                val litros = cursor.getFloat(cursor.getColumnIndexOrThrow(AdminSQLite.COL_REG_LITROS))
                val incidencias = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_REG_INCIDENCIAS))

                lista.add(Registro(id, idParcela, fecha, litros, incidencias))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Estadísticas: Suma de litros en un rango de fechas
    fun sumarLitrosPorRango(fechaInicio: String, fechaFin: String): Float {
        var total = 0f
        val db = dbHelper.readableDatabase
        val query = "SELECT SUM(${AdminSQLite.COL_REG_LITROS}) FROM ${AdminSQLite.TABLA_REGISTROS} WHERE ${AdminSQLite.COL_REG_FECHA} BETWEEN ? AND ?"
        val cursor = db.rawQuery(query, arrayOf(fechaInicio, fechaFin))

        if (cursor.moveToFirst()) {
            total = cursor.getFloat(0)
        }
        cursor.close()
        db.close()
        return total
    }
}