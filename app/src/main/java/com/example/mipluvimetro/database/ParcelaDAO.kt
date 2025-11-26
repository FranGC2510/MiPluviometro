package com.example.mipluvimetro.database

import android.content.ContentValues
import android.content.Context
import com.example.mipluvimetro.models.Parcela

class ParcelaDAO(context: Context) {

    private val dbHelper = AdminSQLite(context)

    fun insertar(parcela: Parcela): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(AdminSQLite.COL_PARCELA_NOMBRE, parcela.nombre)
            put(AdminSQLite.COL_PARCELA_CULTIVO, parcela.cultivo)
            put(AdminSQLite.COL_PARCELA_LAT, parcela.ubicacionLat)
            put(AdminSQLite.COL_PARCELA_LON, parcela.ubicacionLon)
        }
        val id = db.insert(AdminSQLite.TABLA_PARCELAS, null, values)
        db.close()
        return id
    }

    fun obtenerTodas(): List<Parcela> {
        val lista = ArrayList<Parcela>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${AdminSQLite.TABLA_PARCELAS}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_NOMBRE))
                val cultivo = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_CULTIVO))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_LAT))
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(AdminSQLite.COL_PARCELA_LON))

                lista.add(Parcela(id, nombre, cultivo, lat, lon))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun borrar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(AdminSQLite.TABLA_PARCELAS, "${AdminSQLite.COL_PARCELA_ID}=?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}