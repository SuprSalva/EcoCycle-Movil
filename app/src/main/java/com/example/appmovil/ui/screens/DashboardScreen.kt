package com.example.appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.appmovil.network.dto.HistorialItemResponse
import com.example.appmovil.network.dto.UsuarioResponse
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.graphics.graphicsLayer
import com.example.appmovil.ui.components.GlobalLoader
import com.example.appmovil.ui.components.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onViewAllClick: () -> Unit, onNotificationsClick: () -> Unit) {
    var perfil by remember { mutableStateOf<UsuarioResponse?>(null) }
    var historyItems by remember { mutableStateOf<List<HistorialItemResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            GlobalLoader.show("Cargando información...")
            val response = ApiClient.apiService.getPerfil()
            val historyRes = ApiClient.apiService.getHistorial()

            if (response.isSuccessful && historyRes.isSuccessful) {
                perfil = response.body()?.data
                historyItems = historyRes.body()?.data ?: emptyList()
            } else {
                errorMessage = "Error al obtener datos."
            }
        } catch (e: Exception) {
            errorMessage = "No se pudo conectar al servidor: ${e.message}"
        } finally {
            GlobalLoader.hide()
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
                    IconButton(onClick = onNotificationsClick) {
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
                if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                    }
                } else if (!isLoading) {
                    val user = perfil
                    if (user != null) {
                        val puntos = user.puntosDisponibles
                        val nivel = user.nivelActual
                        
                        // Sección de Bienvenida
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hola, ${user.nombre ?: "Usuario"}! 👋",
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
                                        text = nivel ?: "Semilla",
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
                val puntos = perfil?.puntosDisponibles ?: 0.0
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Tarjeta de Balance
                    val infiniteTransition = rememberInfiniteTransition(label = "glow")
                    val glowAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "glowAlpha"
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Resplandor animado
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer {
                                    scaleX = 1.05f
                                    scaleY = 1.1f
                                    alpha = glowAlpha
                                }
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(24.dp)
                                )
                        )
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            ))
                        ) {
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
                                
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.height(36.dp).bounceClick { /* TODO */ }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        Text("Canjear", style = MaterialTheme.typography.labelMedium)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        val botellas = perfil?.totalBotellasRecicladas ?: 0
                        val meta = perfil?.metaActual ?: 1
                        val faltantes = perfil?.faltantesSiguienteNivel ?: 0
                        val progress = if (meta > 0) botellas.toFloat() / meta.toFloat() else 0f
                        
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
                            Text("$botellas", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            Text("Total de botellas recicladas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Meta de nivel", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$botellas / $meta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progress.coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary, // tertiary-fixed-dim approx
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            if (faltantes > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("¡Te faltan $faltantes botellas para el siguiente nivel!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("¡Has alcanzado el máximo nivel, sigue reciclando!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            }
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
                    TextButton(onClick = onViewAllClick) {
                        Text("Ver todo", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            if (!isLoading && historyItems.isEmpty()) {
                item {
                    Text(
                        text = "Aún no tienes actividad reciente. ¡Empieza a reciclar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(historyItems.take(3)) { item ->
                    val isPositive = item.esPositivo
                    val icon = if (isPositive) Icons.Outlined.WaterDrop else Icons.Filled.Redeem
                    val iconBgColor = if (isPositive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    val iconColor = if (isPositive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    val pointsColor = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    
                    ActivityItem(
                        icon = icon,
                        iconBgColor = iconBgColor,
                        iconColor = iconColor,
                        title = item.titulo,
                        subtitle = item.subtitulo,
                        points = item.puntos,
                        pointsColor = pointsColor
                    )
                }
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
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400), initialOffsetY = { it / 2 })
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().bounceClick(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
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
}
