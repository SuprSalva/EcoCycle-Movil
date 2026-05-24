package com.example.appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.network.ApiClient
import com.example.appmovil.network.dto.UsuarioResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var perfil by remember { mutableStateOf<UsuarioResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getPerfil()
            if (response.isSuccessful) {
                perfil = response.body()?.data
            } else {
                errorMessage = "Error al obtener el perfil: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "No se pudo conectar al servidor: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("EcoCycle", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) 
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Recycling, contentDescription = "Logo", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    val user = perfil
                    if (user != null) {
                        val puntos = user.saldoPuntos
                        val nivel = when {
                            puntos < 1000 -> "Novato"
                            puntos < 3000 -> "Intermedio"
                            else -> "Experto"
                        }
                        
                        // Sección de Bienvenida
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hola, ${user.nombre}! 👋",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                // Badge de nivel
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = nivel,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "¿Listo para seguir salvando el planeta hoy?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Grid Layout (Emulated with Row or Column for mobile)
            item {
                val puntos = perfil?.saldoPuntos ?: 0f
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Tarjeta de Balance
                    Card(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Círculo decorativo
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .offset(x = 100.dp, y = (-50).dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                    .align(Alignment.TopEnd)
                            )
                            
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Saldo Disponible", color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelMedium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text("${puntos.toInt()}", color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("pts", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
                                    }
                                }
                                
                                Button(
                                    onClick = { /* TODO */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        contentColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Canjear", style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Tarjeta de Estadísticas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        border = border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("128", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            Text("Total de botellas recicladas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Meta mensual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("128 / 200", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = 128f / 200f,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary, // tertiary-fixed-dim approx
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Actividad Reciente", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("Ver todo", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Actividad 1
            item {
                ActivityItem(
                    icon = Icons.Outlined.WaterDrop,
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    title = "3 botellas",
                    subtitle = "Ayer • Kiosco Plaza",
                    points = "+150 pts",
                    pointsColor = MaterialTheme.colorScheme.primary
                )
            }
            
            // Actividad 2
            item {
                ActivityItem(
                    icon = Icons.Outlined.WaterDrop,
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    title = "5 botellas",
                    subtitle = "12 Oct • Supermercado Central",
                    points = "+250 pts",
                    pointsColor = MaterialTheme.colorScheme.primary
                )
            }

            // Actividad 3
            item {
                ActivityItem(
                    icon = Icons.Filled.Redeem,
                    iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = "Descuento Café",
                    subtitle = "10 Oct • Canje",
                    points = "-500 pts",
                    pointsColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ActivityItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    points: String,
    pointsColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(points, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = pointsColor)
        }
    }
}

private fun border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape) =
    androidx.compose.foundation.BorderStroke(width, color)
