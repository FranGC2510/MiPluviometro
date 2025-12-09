package com.example.mipluvimetro.models

/**
 * Modelo de datos que representa una zona de cultivo o finca.
 *
 * @property id Identificador único en la base de datos (Autogenerado).
 * @property nombre Nombre descriptivo de la parcela (ej: "La Huerta").
 * @property cultivo Tipo de cultivo plantado (ej: "Olivo", "Cereal").
 * @property ubicacionLat Latitud geográfica para la previsión meteorológica.
 * @property ubicacionLon Longitud geográfica para la previsión meteorológica.
 */
data class Parcela(
    val id: Int = 0,
    val nombre: String,
    val cultivo: String,
    val ubicacionLat: Double,
    val ubicacionLon: Double,
    val activa: Boolean = true
)
