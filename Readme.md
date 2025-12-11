# Mi Pluviómetro 

**Gestión Pluviométrica Inteligente para Agricultura**

Aplicación Android nativa desarrollada en Kotlin para la gestión y registro histórico de precipitaciones en fincas agrícolas. Combina almacenamiento local robusto con servicios meteorológicos en tiempo real y capacidades multimedia.

---

## Descripción del Proyecto

"Mi Pluviómetro" digitaliza el tradicional cuaderno de campo. Permite a los agricultores:
1.  Registrar la lluvia caída en sus diferentes parcelas.
2.  Visualizar la evolución histórica mediante gráficas interactivas.
3.  Consultar la previsión meteorológica para planificar riegos.
4.  Aprender a usar el equipamiento mediante tutoriales integrados.

La aplicación sigue una **Arquitectura por Capas** (UI, Datos, Red) para garantizar la escalabilidad y el mantenimiento.

---

## Funcionalidades y Características Técnicas

Este proyecto implementa características avanzadas de desarrollo móvil:

### 1. Procesamiento Multimedia
* **Captura de Imágenes:** Integración con la **Galería Nativa** (usando `ActivityResultContracts`) para asociar fotos reales a cada parcela.
* **Gestión de URIs:** Persistencia eficiente de rutas de imagen en base de datos sin bloatware (no se guardan BLOBs, sino referencias `content://`).

### 2. Reproducción de Video y Audio
* **Video Tutorial (Ayuda):** Implementación de **ExoPlayer (Media3)**, el reproductor estándar profesional de Android, con controles integrados y gestión eficiente de recursos.
* **Feedback Sonoro:** Uso de `MediaPlayer` para emitir efectos de sonido de confirmación al guardar registros, mejorando la experiencia de usuario (UX).

### 3. Exportación y Sistema de Archivos
* **Generación de Informes:** Capacidad para convertir la gráfica estadística en un archivo de imagen **JPEG**.
* **Almacenamiento:** Uso de la API `MediaStore` para guardar la imagen en la galería pública del dispositivo, cumpliendo con los estándares de seguridad de **Scoped Storage** (Android 10+).

### 4. Gráficos y Animaciones
* **Visualización de Datos:** Integración de la librería **MPAndroidChart** para generar gráficas de barras agrupadas por meses.
* **Animaciones:**
    * Animación de entrada en el eje Y para las gráficas.
    * Transiciones suaves entre fragmentos.

### 5. Conectividad API REST
* **Previsión Meteorológica:** Conexión con **OpenWeatherMap API**.
* **Networking:** Uso de **Retrofit 2** + **Gson** para la comunicación HTTP y parseo de JSON.
* **Corrutinas:** Gestión asíncrona de peticiones de red para no bloquear el hilo principal (UI).

### 6. Persistencia de Datos (SQLite)
* **Base de Datos Relacional:** Uso de `SQLiteOpenHelper`.
* **Integridad Referencial:** Implementación de **Borrado Lógico (Soft Delete)**. Al borrar una finca, esta desaparece de la vista pero sus datos históricos de lluvia se conservan para no alterar las estadísticas globales.

---

## Stack Tecnológico

* **Lenguaje:** Kotlin
* **UI:** XML Layouts, Material Design Components.
* **Base de Datos:** SQLite Nativo.
* **Red:** Retrofit 2, OkHttp.
* **Multimedia:** AndroidX Media3 (ExoPlayer), MediaPlayer.
* **Gráficos:** MPAndroidChart.
* **Concurrencia:** Kotlin Coroutines & Lifecycle Scopes.

---

## Guía de Instalación

1.  Clonar el repositorio.
2.  Abrir en **Android Studio** (Recomendado: Ladybug o superior).
3.  **Configurar API Key:**
    * Obtén una clave gratuita en [OpenWeatherMap](https://openweathermap.org/).
    * Abre `ui/ClimaFragment.kt` y pega tu clave en la variable `API_KEY`.
4.  Sincronizar Gradle y ejecutar en emulador o dispositivo físico (Min SDK 24).

---

## Futuras Mejoras (Roadmap)

* [ ] **Geolocalización GPS:** Usar `FusedLocationProvider` para capturar coordenadas exactas de la finca.
* [ ] **Backup Cloud:** Sincronización con Firebase para respaldo de datos.
* [ ] **Notificaciones Push:** Avisos de tormenta basados en la ubicación.
* [ ] **Modo Oscuro:** Adaptación completa del tema `DayNight`.

---

**Desarrollado por:** Fco Javier García Cañero
**Asignatura:** Desarrollo de Aplicaciones Móviles