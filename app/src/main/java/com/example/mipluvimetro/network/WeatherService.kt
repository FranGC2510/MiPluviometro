package com.example.mipluvimetro.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // Definimos el endpoint para la previsión de 5 días / 3 horas
    @GET("data/2.5/forecast")
    suspend fun obtenerPronostico(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Para obtener Celsius
        @Query("lang") lang: String = "es"        // Descripción en español
    ): Response<WeatherResponse>
}