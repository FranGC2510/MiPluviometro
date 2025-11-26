package com.example.mipluvimetro.models

data class Parcela(
    val id: Int = 0,             // ID autogenerado (0 por defecto al crear)
    val nombre: String,          // Ej: "La Huerta"
    val cultivo: String,         // Ej: "Olivo"
    val ubicacionLat: Double,    // Coordenada Latitud
    val ubicacionLon: Double     // Coordenada Longitud
)
