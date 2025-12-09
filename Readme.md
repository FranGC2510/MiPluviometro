# Mi Pluviómetro 

**Gestión Pluviométrica Inteligente para Agricultores**

Aplicación Android nativa desarrollada en Kotlin que permite a los agricultores llevar un registro histórico preciso de las precipitaciones en sus fincas y consultar la previsión meteorológica en tiempo real para optimizar la toma de decisiones de riego.

---

## Estado del Proyecto
**Versión Actual:** 1.0 (MVP Funcional)
**Estado:**  Estable / En fase de optimización

---

## Funcionalidades Implementadas

### 1. Motor de Datos (Backend Local)
* **Persistencia SQLite:** Base de datos relacional nativa optimizada.
* **Integridad de Datos:** Implementación de **Borrado Lógico (Soft Delete)** para preservar el histórico de lluvias incluso si se elimina una finca.
* **Arquitectura DAO:** Patrón de acceso a datos separado (`ParcelaDAO` y `RegistroDAO`) con soporte para operaciones CRUD completas.
* **Migraciones Seguras:** Sistema `onUpgrade` preparado para actualizaciones de esquema sin pérdida de datos.

### 2. Conectividad y Clima (API)
* **Cliente Retrofit:** Capa de red robusta para comunicación HTTP.
* **OpenWeatherMap Integration:** Conexión en tiempo real para obtener previsiones meteorológicas a 5 días (bloques de 3 horas).
* **Buscador de Ciudades:** Interfaz para consultar el clima de cualquier localidad (ej: "Cordoba,ES").

### 3. Interfaz de Usuario (UI/UX)
* **Diseño Hidro-Moderno:** Paleta de colores personalizada, temas `DayNight` y componentes Material Design.
* **Navegación:** Flujo fluido mediante **Jetpack Navigation Component**.
* **Dashboard (Inicio):**
    * Tarjetas de resumen estadístico (Mes Actual y Año Natural).
    * Lista de últimos registros con edición (click) y borrado (long click).
* **Gestión de Parcelas:**
    * CRUD completo de fincas.
    * Diálogos modales para alta y edición de datos.
* **Previsión Meteorológica:**
    * Vista detallada con iconos, temperatura y probabilidad de lluvia.
    * Indicadores visuales dinámicos (solo muestra la etiqueta de lluvia si se esperan precipitaciones).

---

## Futuras Mejoras (Roadmap)

* [ ] **Geolocalización Real:** Implementar la API de *FusedLocationProvider* para capturar las coordenadas exactas de la finca mediante GPS en lugar de simulación.
* [ ] **Gráficos Estadísticos:** Visualización de datos mediante librerías como MPAndroidChart (curvas de lluvia anual, comparativas mensuales).
* [ ] **Exportación de Datos:** Generación de informes en PDF o CSV para compartir o imprimir el registro anual.
* [ ] **Notificaciones:** Alertas locales para recordar anotar la lluvia o avisos de tormenta basados en la API.
* [ ] **Backup en la Nube:** Sincronización con Firebase o Google Drive para no perder datos si se cambia de móvil.

---

## Stack Tecnológico

* **Lenguaje:** Kotlin
* **Base de Datos:** SQLite (Nativo con `SQLiteOpenHelper`)
* **Red:** Retrofit 2 + Gson Converter
* **Concurrencia:** Kotlin Coroutines (Gestión asíncrona de BD y Red)
* **Navegación:** Android Jetpack Navigation
* **Diseño:** Material Design Components (XML Layouts)

---

## Configuración para Desarrolladores

Para compilar el proyecto, asegúrate de tener:

1.  **Android Studio:** Versión Koala o superior recomendada.
2.  **Min SDK:** 24 (Android 7.0 Nougat).
3.  **API Key:** Necesitas una clave gratuita de [OpenWeatherMap](https://openweathermap.org/).
    * Insértala en `ui/ClimaFragment.kt` en la constante `API_KEY`.

---

## Estructura del Proyecto

```text
com.ejemplo.mipluviometro
├── adapter      # LluviaAdapter, ParcelaAdapter, ClimaAdapter
├── database     # AdminSQLite, ParcelaDAO, RegistroDAO
├── model        # Data Classes (Parcela, Registro)
├── network      # RetrofitClient, WeatherService, Modelos API
└── ui           # MainActivity, HomeFragment, ParcelasFragment, ClimaFragment