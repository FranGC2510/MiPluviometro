package com.example.mipluvimetro.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLite(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "pluviometro.db"
        private const val DATABASE_VERSION = 1

        // --- Tabla Parcelas ---
        const val TABLA_PARCELAS = "parcelas"
        const val COL_PARCELA_ID = "id"
        const val COL_PARCELA_NOMBRE = "nombre"
        const val COL_PARCELA_CULTIVO = "cultivo"
        const val COL_PARCELA_LAT = "ubicacion_lat"
        const val COL_PARCELA_LON = "ubicacion_lon"

        // --- Tabla Registros Lluvia ---
        const val TABLA_REGISTROS = "registros_lluvia"
        const val COL_REG_ID = "id"
        const val COL_REG_PARCELA_ID = "id_parcela" // FK
        const val COL_REG_FECHA = "fecha"
        const val COL_REG_LITROS = "litros"
        const val COL_REG_INCIDENCIAS = "incidencias"
    }

    // Se ejecuta UNA SOLA VEZ cuando la base de datos no existe
    override fun onCreate(db: SQLiteDatabase?) {
        val crearParcelas = """
            CREATE TABLE $TABLA_PARCELAS (
                $COL_PARCELA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PARCELA_NOMBRE TEXT,
                $COL_PARCELA_CULTIVO TEXT,
                $COL_PARCELA_LAT REAL,
                $COL_PARCELA_LON REAL
            )
        """.trimIndent()
        db?.execSQL(crearParcelas)

        val crearRegistros = """
            CREATE TABLE $TABLA_REGISTROS (
                $COL_REG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REG_PARCELA_ID INTEGER,
                $COL_REG_FECHA TEXT,
                $COL_REG_LITROS REAL,
                $COL_REG_INCIDENCIAS TEXT,
                FOREIGN KEY($COL_REG_PARCELA_ID) REFERENCES $TABLA_PARCELAS($COL_PARCELA_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db?.execSQL(crearRegistros)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Por simplicidad, borramos y creamos de nuevo (cuidado en producción real)
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_REGISTROS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_PARCELAS")
        onCreate(db)
    }
}