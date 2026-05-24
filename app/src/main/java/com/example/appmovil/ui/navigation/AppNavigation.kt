package com.example.appmovil.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appmovil.QrActivity
import com.example.appmovil.ui.screens.DashboardScreen
import com.example.appmovil.ui.screens.MenuScreen
import com.example.appmovil.ui.screens.RecompensasScreen

@Composable
fun AppNavigation(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                    // Tab: Inicio
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Tab: Escanear
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear") },
                        label = { Text("Escanear") },
                        selected = false,
                        onClick = {
                            val intent = Intent(context, QrActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    // Tab: Recompensas
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Star, contentDescription = "Premios") },
                        label = { Text("Premios") },
                        selected = currentDestination?.hierarchy?.any { it.route == "recompensas" } == true,
                        onClick = {
                            navController.navigate("recompensas") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Tab: Menú
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Menú") },
                        label = { Text("Perfil") },
                        selected = currentDestination?.hierarchy?.any { it.route == "menu" } == true,
                        onClick = {
                            navController.navigate("menu") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen() }
            composable("recompensas") { RecompensasScreen() }
            composable("menu") { MenuScreen(onLogout) }
        }
    }
}
