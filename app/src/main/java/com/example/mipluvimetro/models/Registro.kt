package com.example.mipluvimetro.models

/**
 * Modelo de datos que representa un evento de lluvia diario.
 *
 * @property id Identificador único del registro.
 * @property idParcela Clave foránea que vincula este registro con una [Parcela].
 * @property fecha Fecha del evento en formato ISO-8601 (YYYY-MM-DD).
 * @property litros Cantidad de precipitación en litros por metro cuadrado (mm).
 * @property incidencias Notas opcionales sobre daños (ej: "Granizo", "Viento").
 */
data class Registro(
    val id: Int = 0,
    val idParcela: Int,
    val fecha: String,
    val litros: Float,
    val incidencias: String
)
