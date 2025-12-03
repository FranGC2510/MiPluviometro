package com.example.mipluvimetro.network

import com.google.gson.annotations.SerializedName

/**
 * Clase raíz de la respuesta JSON de OpenWeatherMap (Endpoint /forecast).
 * Actúa como contenedor principal de la lista de predicciones.
 *
 * @property list Lista que contiene la previsión meteorológica en bloques de 3 horas para los próximos 5 días.
 */
data class WeatherResponse(
    val list: List<Prevision>
)

/**
 * Representa el estado del tiempo en un momento específico (bloque de 3 horas).
 *
 * @property fechaHora La fecha y hora del pronóstico en formato texto (UTC).
 * Formato estándar: "YYYY-MM-DD HH:MM:SS" (ej: "2023-11-20 15:00:00").
 * @property main Contiene los datos principales atmosféricos (temperatura, presión, humedad).
 * @property rain Objeto con datos de lluvia. IMPORTANTE: Este campo es null si no hay previsión de lluvia.
 * @property weather Lista de descripciones del clima (ej: nublado, lluvia ligera). Es una lista, pero usualmente tomamos el primer elemento.
 */
data class Prevision(
    @SerializedName("dt_txt") val fechaHora: String,
    val main: MainData,
    val rain: RainData?,
    val weather: List<WeatherDescription>
)

/**
 * Datos atmosféricos principales.
 *
 * @property temp Temperatura actual en la unidad solicitada (Celsius si usamos units=metric).
 * @property humidity Porcentaje de humedad relativa (0-100).
 */
data class MainData(
    val temp: Double,      // Temperatura
    val humidity: Int      // Humedad
)

/**
 * Datos específicos de volumen de precipitación.
 *
 * Nota técnica:
 * La API devuelve una clave JSON llamada "3h" (ej: { "3h": 2.5 }).
 * Como en Kotlin las variables no pueden empezar por número, usamos @SerializedName
 * para mapear la clave JSON "3h" a nuestra variable "litros3h".
 *
 * @property litros3h Volumen de lluvia en milímetros (o litros/m2) en las últimas 3 horas.
 * Es nullable (?) y tiene valor por defecto 0.0 para evitar nulos si el objeto existe pero está vacío.
 */
data class RainData(
    @SerializedName("3h") val litros3h: Double? = 0.0
)

/**
 * Descripción textual del clima, útil para mostrar al usuario o elegir iconos.
 *
 * @property description Descripción detallada (ej: "lluvia ligera", "cielo claro").
 * Viene traducida si añadimos el parámetro &lang=es en la petición.
 */
data class WeatherDescription(
    val description: String // Ej: "lluvia ligera"
)
