# Mi Pluviómetro 

**Gestión Pluviométrica para Agricultores**

Una aplicación Android nativa desarrollada en Kotlin, diseñada para ayudar a los agricultores a llevar un registro preciso de las precipitaciones en sus fincas y consultar la previsión meteorológica para la toma de decisiones de riego.

---

## Estado del Proyecto
**Versión Actual:** 0.5 (Base Tecnológica y Dashboard UI)  
**Estado:** 🚧 En Desarrollo Activo

---

## Funcionalidades Implementadas

### 1. Motor de Datos (Backend Local)
* **Persistencia SQLite:** Base de datos relacional nativa completamente configurada.
* **Arquitectura DAO:** Separación de lógica de acceso a datos (`ParcelaDAO` y `RegistroDAO`).
* **Modelos de Datos:** Estructuras definidas para `Parcela` (Fincas) y `Registro` (Lluvias).
* **Gestión de Migraciones:** Sistema `onUpgrade` preparado para actualizaciones incrementales en producción sin pérdida de datos.

### 2. Conectividad (Red)
* **Cliente Retrofit:** Configuración completa para conexión HTTP.
* **Integración OpenWeatherMap:** Modelos DTO (`WeatherResponse`) y servicio (`WeatherService`) listos para consultar previsiones meteorológicas de 5 días.

### 3. Interfaz de Usuario (UI/UX)
* **Diseño Hidro-Moderno:** Paleta de colores personalizada (Azules/Cian) y temas `DayNight` configurados.
* **Navegación:** Implementación de **Jetpack Navigation Component** con `BottomNavigationView` (3 secciones: Inicio, Parcelas, Previsión).
* **Dashboard (Inicio):**
    * Cálculo automático de **Totales del Mes Actual**.
    * Cálculo automático del **Año Natural**.
    * Lista (`RecyclerView`) de últimos registros con adaptador personalizado.
    * Diseño de tarjetas (`CardView`) con indicadores visuales de incidencias.

---

## En Proceso / Pendiente

* [ ] **Gestión de Parcelas (UI):** Pantalla para añadir, editar y eliminar fincas (CRUD visual).
* [ ] **Formulario de Registro:** Botón flotante y diálogos para introducir los litros de lluvia diarios.
* [ ] **Pantalla de Clima (UI):** Conectar la capa de Retrofit con la vista para mostrar la gráfica o lista de pronóstico del tiempo.
* [ ] **Geolocalización:** Implementación de permisos GPS para autocompletar coordenadas de las parcelas.
* [ ] **Exportación:** Funcionalidad para compartir reportes (Email/PDF).

---

## Stack Tecnológico

* **Lenguaje:** Kotlin
* **Base de Datos:** SQLite (Nativo con `SQLiteOpenHelper`)
* **Red:** Retrofit 2 + Gson
* **Asincronía:** Kotlin Coroutines
* **Navegación:** Android Jetpack Navigation
* **Diseño:** Material Design Components (XML Layouts)

---

## Requisitos de Configuración

Para compilar el proyecto, asegúrate de tener:

1.  **API Key de OpenWeatherMap:** Debes añadir tu clave en `WeatherService.kt` o en un archivo de propiedades seguro.
2.  **Android Studio:** Koala o superior recomendado.
3.  **Min SDK:** 24 (Android 7.0).

---

## Estructura del Proyecto

```text
com.ejemplo.mipluviometro
├── adapter      # Adaptadores para RecyclerView (LluviaAdapter)
├── database     # AdminSQLite y DAOs (Lógica de BD)
├── model        # Data Classes (Parcela, Registro)
├── network      # Cliente Retrofit y Modelos de API (Weather)
└── ui           # Fragmentos y MainActivity