package com.example.mipluvimetro.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton (única instancia) que gestiona la configuración de red.
 *
 * - Configura la URL base (api.openweathermap.org).
 * - Inicializa el convertidor Gson (JSON a Kotlin).
 * - Provee la instancia lista para usar del servicio [WeatherService].
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    // Solo se crea cuando se llama por primera vez
    val servicio: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}