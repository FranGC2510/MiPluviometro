package com.example.mipluvimetro.network

import com.google.gson.annotations.SerializedName

// Estructura principal de la respuesta de OpenWeatherMap (Forecast 5 days)
data class WeatherResponse(
    val list: List<Prevision> // Lista de previsiones cada 3 horas
)

data class Prevision(
    @SerializedName("dt_txt") val fechaHora: String, // Ej: "2023-11-20 15:00:00"
    val main: MainData,
    val rain: RainData?,     // Puede ser nulo si no va a llover
    val weather: List<WeatherDescription>
)

data class MainData(
    val temp: Double,      // Temperatura
    val humidity: Int      // Humedad
)

data class RainData(
    @SerializedName("3h") val litros3h: Double? = 0.0 // Cantidad de lluvia en las últimas 3h
)

data class WeatherDescription(
    val description: String // Ej: "lluvia ligera"
)
