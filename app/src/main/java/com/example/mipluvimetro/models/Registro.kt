package com.example.mipluvimetro.models

data class Registro(
    val id: Int = 0,
    val idParcela: Int,          // Clave foránea: une este registro con una Parcela
    val fecha: String,           // Guardaremos la fecha como texto (ISO 8601: YYYY-MM-DD)
    val litros: Float,           // Cantidad de lluvia
    val incidencias: String      // Notas opcionales
)
