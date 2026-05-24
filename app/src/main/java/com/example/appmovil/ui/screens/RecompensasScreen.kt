package com.example.appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.network.ApiClient
import com.example.appmovil.network.dto.CanjearRequest
import kotlinx.coroutines.launch

data class RecompensaMock(
    val id: String,
    val titulo: String,
    val puntos: Int,
    val icon: ImageVector,
    val locked: Boolean = false,
    val ptsFaltantes: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasScreen(onNotificationsClick: () -> Unit) {
    var recompensas by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<List<RecompensaMock>>(emptyList()) }
    var selectedCategory by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Todos") }
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    var errorMessage by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            // Fetch rewards
            val responseRecompensas = ApiClient.apiService.getRecompensas()
            // Fetch points to calculate locked status
            val responsePerfil = ApiClient.apiService.getPerfil()
            
            if (responseRecompensas.isSuccessful && responsePerfil.isSuccessful) {
                val saldoActual = responsePerfil.body()?.data?.saldoPuntos?.toInt() ?: 0
                val data = responseRecompensas.body()?.data ?: emptyList()
                
                recompensas = data.map { r ->
                    val icon = when {
                        r.nombre.contains("Café", ignoreCase = true) -> Icons.Filled.LocalCafe
                        r.nombre.contains("Super", ignoreCase = true) -> Icons.Filled.ShoppingCart
                        r.nombre.contains("Bus", ignoreCase = true) -> Icons.Filled.DirectionsBus
                        r.nombre.contains("Siembra", ignoreCase = true) || r.nombre.contains("Eco", ignoreCase = true) -> Icons.Filled.Eco
                        else -> Icons.Filled.CardGiftcard
                    }
                    val locked = saldoActual < r.costoPuntos.toInt()
                    val faltantes = if (locked) r.costoPuntos.toInt() - saldoActual else 0
                    
                    RecompensaMock(
                        id = r.id,
                        titulo = r.nombre,
                        puntos = r.costoPuntos.toInt(),
                        icon = icon,
                        locked = locked,
                        ptsFaltantes = faltantes
                    )
                }
            } else {
                errorMessage = "Error al cargar recompensas."
            }
        } catch (e: Exception) {
            errorMessage = "No se pudo conectar al servidor."
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
                    IconButton(onClick = onNotificationsClick) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Header & Balance
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Premios",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Tu Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TU BALANCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.GeneratingTokens, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "1,250 pts",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filtros (Scrollable Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChipCustom(text = "Todos", icon = Icons.Filled.Apps, isSelected = selectedCategory == "Todos", onClick = { selectedCategory = "Todos" })
                FilterChipCustom(text = "Cafeterías", icon = Icons.Filled.LocalCafe, isSelected = selectedCategory == "Cafeterías", onClick = { selectedCategory = "Cafeterías" })
                FilterChipCustom(text = "Supermercados", icon = Icons.Filled.ShoppingCart, isSelected = selectedCategory == "Supermercados", onClick = { selectedCategory = "Supermercados" })
                FilterChipCustom(text = "Transporte", icon = Icons.Filled.DirectionsBus, isSelected = selectedCategory == "Transporte", onClick = { selectedCategory = "Transporte" })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rewards Grid
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }
            } else {
                val filteredRecompensas = when (selectedCategory) {
                    "Cafeterías" -> recompensas.filter { it.titulo.contains("Café", ignoreCase = true) }
                    "Supermercados" -> recompensas.filter { it.titulo.contains("Super", ignoreCase = true) }
                    "Transporte" -> recompensas.filter { it.titulo.contains("Bus", ignoreCase = true) }
                    else -> recompensas
                }

                if (filteredRecompensas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay premios disponibles en esta categoría en este momento.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(filteredRecompensas) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .alpha(if (item.locked) 0.6f else 1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Icon Box (Reemplazando imagen por icono)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 3f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                if (item.locked) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = CircleShape,
                                            shadowElevation = 2.dp
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Faltan ${item.ptsFaltantes} pts", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Info
                            Column {
                                Text(
                                    text = item.titulo,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.GeneratingTokens, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${item.puntos} pts",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }

                            // Button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val canjeRes = ApiClient.apiService.canjearRecompensa(CanjearRequest(item.id))
                                            if (canjeRes.isSuccessful) {
                                                // Idealmente recargar la pantalla o mostrar un snackbar
                                                errorMessage = "¡Canje exitoso!"
                                            } else {
                                                errorMessage = "Error al canjear."
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Error de conexión."
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                enabled = !item.locked,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Canjear", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
}

@Composable
fun FilterChipCustom(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
        modifier = Modifier.height(36.dp).clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
