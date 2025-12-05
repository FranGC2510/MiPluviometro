package com.example.mipluvimetro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // 2. Encontrar el menú inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 3. Buscar el Controlador de Navegación (NavHost)
        // Usamos supportFragmentManager porque estamos en una Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 4. Conectar el menú con el controlador
        bottomNav.setupWithNavController(navController)
    }
}