package com.example.mipluvimetro.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Clase de bajo nivel para la gestión del esquema de la base de datos.
 * Hereda de [SQLiteOpenHelper].
 *
 * Responsabilidades:
 * 1. Crear las tablas cuando la App se instala.
 * 2. Gestionar actualizaciones de versión.
 * 3. Proveer constantes públicas para los nombres de tablas y columnas.
 */
class AdminSQLite(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "pluviometro.db"
        private const val DATABASE_VERSION = 2

        // Tabla Parcelas
        const val TABLA_PARCELAS = "parcelas"
        const val COL_PARCELA_ID = "id"
        const val COL_PARCELA_NOMBRE = "nombre"
        const val COL_PARCELA_CULTIVO = "cultivo"
        const val COL_PARCELA_LAT = "ubicacion_lat"
        const val COL_PARCELA_LON = "ubicacion_lon"
        const val COL_PARCELA_ACTIVA = "activa"

        // Tabla Registros Lluvia
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
                $COL_PARCELA_LON REAL,
                $COL_PARCELA_ACTIVA INTEGER DEFAULT 1
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

    /**
     * Gestión de actualizaciones de la base de datos (Migraciones).
     * Nunca borramos tablas (DROP) si contienen datos de usuario.
     * Aplicamos cambios incrementales (ALTER TABLE, CREATE TABLE nuevas) versión a versión.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var versionActual = oldVersion
        if(versionActual < 2){
            db?.execSQL("ALTER TABLE $TABLA_PARCELAS ADD COLUMN $COL_PARCELA_ACTIVA INTEGER DEFAULT 1")
            versionActual = 2
        }
    }
}