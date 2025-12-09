package com.example.mipluvimetro.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de la API de OpenWeatherMap.
 * Utiliza anotaciones de Retrofit (@GET, @Query) para construir la petición HTTP.
 *
 * Funciones:
 * - obtenerPronostico: Descarga la previsión de 5 días / 3 horas.
 */
interface WeatherService {
    // Definimos el endpoint para la previsión de 5 días / 3 horas
    @GET("data/2.5/forecast")
    suspend fun obtenerPronosticoPorCoordenadas(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Para obtener Celsius
        @Query("lang") lang: String = "es"        // Descripción en español
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun obtenerPronosticoPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): Response<WeatherResponse>
}