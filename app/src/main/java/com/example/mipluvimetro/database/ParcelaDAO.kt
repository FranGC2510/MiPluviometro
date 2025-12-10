package com.example.mipluvimetro.database

import android.content.ContentValues
import android.content.Context
import com.example.mipluvimetro.models.Parcela

/**
 * Data Access Object (DAO) para la entidad [Parcela].
 *
 * Encapsula todas las operaciones CRUD (Crear, Leer, Borrar) relacionadas
 * con las parcelas. Aísla la lógica SQL del resto de la aplicación.
 *
 * @param context Contexto necesario para abrir la conexión a BD.
 */
class ParcelaDAO(context: Context) {

    private val dbHelper = AdminSQLite(context)

    fun insertar(parcela: Parcela): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AdminSQLite.COL_PARCELA_NOMBRE, parcela.nombre)
            put(AdminSQLite.COL_PARCELA_CULTIVO, parcela.cultivo)
            put(AdminSQLite.COL_PARCELA_LAT, parcela.ubicacionLat)
            put(AdminSQLite.COL_PARCELA_LON, parcela.ubicacionLon)
            put(AdminSQLite.COL_PARCELA_ACTIVA, if (parcela.activa) 1 else 0)
            put(AdminSQLite.COL_PARCELA_IMAGEN, parcela.imagenUri)
        }
        val id = db.insert(AdminSQLite.TABLA_PARCELAS, null, values)
        db.close()
        return id
    }

    fun obtenerTodas(): List<Parcela> {
        return ejecutarConsulta("SELECT * FROM ${AdminSQLite.TABLA_PARCELAS} WHERE ${AdminSQLite.COL_PARCELA_ACTIVA} = 1")
    }

    fun obtenerTodasIncluidoBorradas(): List<Parcela> {
        return ejecutarConsulta("SELECT * FROM ${AdminSQLite.TABLA_PARCELAS}")
    }

    private fun ejecutarConsulta(query: String): List<Parcela> {
        val lista = ArrayList<Parcela>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_NOMBRE))
                val cultivo = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_CULTIVO))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_LAT))
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_LON))
                val activaInt = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_ACTIVA))
                val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_IMAGEN))

                lista.add(Parcela(id, nombre, cultivo, lat, lon, activaInt == 1, imagenUri))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun borrar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(AdminSQLite.COL_PARCELA_ACTIVA, 0) // La marcamos como inactiva

        val filas = db.update(
            AdminSQLite.TABLA_PARCELAS,
            values,
            "${AdminSQLite.COL_PARCELA_ID}=?",
            arrayOf(id.toString())
        )
        db.close()
        return filas
    }
}