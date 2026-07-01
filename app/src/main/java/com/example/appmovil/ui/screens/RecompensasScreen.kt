package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.appmovil.network.ApiClient
import com.example.appmovil.network.dto.CanjearRequest
import com.example.appmovil.network.dto.CanjeUsuarioResponse
import com.example.appmovil.ui.components.GlobalLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RecompensaMock(
    val id: String,
    val titulo: String,
    val puntos: Int,
    val icon: ImageVector,
    val locked: Boolean = false,
    val ptsFaltantes: Int = 0,
    val imagenUrl: String? = null
)

// ---------- Datos de celebración tras el canje ----------
data class CelebrationData(
    val recompensaNombre: String,
    val codigoCanje: String,
    val nuevoSaldo: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasScreen(onNotificationsClick: () -> Unit) {
    var recompensas by remember { mutableStateOf<List<RecompensaMock>>(emptyList()) }
    var saldoPuntosUsuario by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedTab by remember { mutableStateOf(0) }   // 0 = Catálogo, 1 = Mis Canjes
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var celebrationData by remember { mutableStateOf<CelebrationData?>(null) }
    var misCanjes by remember { mutableStateOf<List<CanjeUsuarioResponse>>(emptyList()) }
    var isLoadingCanjes by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Función para recargar recompensas (se llama también tras un canje exitoso)
    suspend fun recargarRecompensas() {
        try {
            val responseRecompensas = ApiClient.apiService.getRecompensas()
            val responsePerfil = ApiClient.apiService.getPerfil()
            if (responseRecompensas.isSuccessful && responsePerfil.isSuccessful) {
                val saldoActual = responsePerfil.body()?.data?.puntosDisponibles?.toInt() ?: 0
                saldoPuntosUsuario = saldoActual
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
                        ptsFaltantes = faltantes,
                        imagenUrl = r.imagenUrl
                    )
                }
            } else {
                errorMessage = "Error al cargar recompensas."
            }
        } catch (e: Exception) {
            errorMessage = "No se pudo conectar al servidor."
        }
    }

    LaunchedEffect(Unit) {
        try {
            GlobalLoader.show("Cargando recompensas...")
            recargarRecompensas()
        } finally {
            GlobalLoader.hide()
            isLoading = false
        }
    }

    // Cargar "Mis Canjes" cuando se selecciona esa pestaña
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            isLoadingCanjes = true
            try {
                val res = ApiClient.apiService.getMisCanjes()
                if (res.isSuccessful) {
                    misCanjes = res.body()?.data ?: emptyList()
                }
            } catch (_: Exception) { }
            finally {
                isLoadingCanjes = false
            }
        }
    }

    // ---- Overlay de celebración ----
    if (celebrationData != null) {
        CelebrationOverlay(
            data = celebrationData!!,
            onDismiss = { celebrationData = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("EcoCycle", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
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
                                    text = "$saldoPuntosUsuario pts",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs: Catálogo / Mis Canjes
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Catálogo",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(Icons.Filled.CardGiftcard, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Mis Canjes",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido según pestaña
            when (selectedTab) {
                0 -> CatalogoTab(
                    recompensas = recompensas,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    onCanjear = { item ->
                        coroutineScope.launch {
                            try {
                                GlobalLoader.show("Canjeando...")
                                val canjeRes = ApiClient.apiService.canjearRecompensa(CanjearRequest(item.id))
                                if (canjeRes.isSuccessful) {
                                    val body = canjeRes.body()?.data
                                    // Extraer datos de la respuesta
                                    @Suppress("UNCHECKED_CAST")
                                    val map = body as? Map<String, Any>
                                    val codigo = map?.get("codigoCanje")?.toString() ?: "------"
                                    val nuevoSaldo = (map?.get("nuevoSaldo") as? Double)?.toInt()
                                        ?: (saldoPuntosUsuario - item.puntos)
                                    // Recargar catálogo en background
                                    recargarRecompensas()
                                    // Mostrar celebración
                                    celebrationData = CelebrationData(
                                        recompensaNombre = item.titulo,
                                        codigoCanje = codigo,
                                        nuevoSaldo = nuevoSaldo
                                    )
                                } else {
                                    errorMessage = "No se pudo completar el canje."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error de conexión."
                            } finally {
                                GlobalLoader.hide()
                            }
                        }
                    }
                )
                1 -> MisCanjesTab(
                    canjes = misCanjes,
                    isLoading = isLoadingCanjes
                )
            }
        }
    }
}

// ============================================================
//  TAB 0 – Catálogo de recompensas
// ============================================================
@Composable
fun CatalogoTab(
    recompensas: List<RecompensaMock>,
    isLoading: Boolean,
    errorMessage: String?,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onCanjear: (RecompensaMock) -> Unit
) {
    Column {
        // Filtros (Scrollable Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChipCustom(text = "Todos", icon = Icons.Filled.Apps, isSelected = selectedCategory == "Todos", onClick = { onCategoryChange("Todos") })
            FilterChipCustom(text = "Cafeterías", icon = Icons.Filled.LocalCafe, isSelected = selectedCategory == "Cafeterías", onClick = { onCategoryChange("Cafeterías") })
            FilterChipCustom(text = "Supermercados", icon = Icons.Filled.ShoppingCart, isSelected = selectedCategory == "Supermercados", onClick = { onCategoryChange("Supermercados") })
            FilterChipCustom(text = "Transporte", icon = Icons.Filled.DirectionsBus, isSelected = selectedCategory == "Transporte", onClick = { onCategoryChange("Transporte") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        } else if (!isLoading) {
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
                        textAlign = TextAlign.Center
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
                        RecompensaCard(item = item, onCanjear = { onCanjear(item) })
                    }
                }
            }
        }
    }
}

// ============================================================
//  Tarjeta individual de recompensa
// ============================================================
@Composable
fun RecompensaCard(item: RecompensaMock, onCanjear: () -> Unit) {
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
            // Imagen/Icono
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!item.imagenUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = item.imagenUrl,
                        contentDescription = item.titulo,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
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

            Button(
                onClick = onCanjear,
                modifier = Modifier.fillMaxWidth().height(40.dp),
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

// ============================================================
//  TAB 1 – Mis Canjes
// ============================================================
@Composable
fun MisCanjesTab(
    canjes: List<CanjeUsuarioResponse>,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (canjes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    Icons.Filled.ConfirmationNumber,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = "Aún no has canjeado ningún premio.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(canjes) { canje ->
            CanjeItem(canje = canje)
        }
    }
}

// ============================================================
//  Ítem de canje individual en "Mis Canjes"
// ============================================================
@Composable
fun CanjeItem(canje: CanjeUsuarioResponse) {
    val isReclamado = canje.reclamado
    val statusColor = if (isReclamado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    val statusBgColor = if (isReclamado)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.tertiaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (isReclamado) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono + nombre
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!canje.recompensaImagenUrl.isNullOrEmpty()) {
                            coil.compose.AsyncImage(
                                model = canje.recompensaImagenUrl,
                                contentDescription = canje.recompensaNombre,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Filled.CardGiftcard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = canje.recompensaNombre,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.GeneratingTokens, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${canje.puntosUsados.toInt()} pts",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                // Badge de estatus
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isReclamado) Icons.Filled.CheckCircle else Icons.Filled.HourglassBottom,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = statusColor
                        )
                        Text(
                            text = if (isReclamado) "Reclamado" else "Pendiente",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // Código de canje destacado
            if (!isReclamado) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MUESTRA ESTE CÓDIGO AL ADMINISTRADOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = canje.codigoCanje.ifEmpty { "N/A" },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 4.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Código de canje:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = canje.codigoCanje.ifEmpty { "------" },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Fecha de canje y fecha de reclamación (si aplica)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Canjeado: ${formatFecha(canje.fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (isReclamado && canje.fechaReclamado != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reclamado: ${formatFecha(canje.fechaReclamado)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
//  Overlay de celebración post-canje
// ============================================================
@Composable
fun CelebrationOverlay(data: CelebrationData, onDismiss: () -> Unit) {
    // Auto-dismiss después de 4 segundos
    LaunchedEffect(Unit) {
        delay(4000L)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "celebrationPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(initialScale = 0.6f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clickable(onClick = {}), // Evita que el click se propague al dismiss
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Icono animado con fondo degradado
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .scale(pulse)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        // Título
                        Text(
                            text = "¡Enhorabuena!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        // Premio canjeado
                        Text(
                            text = "Has canjeado con éxito:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = data.recompensaNombre,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Código de canje
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tu código de canje:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = data.codigoCanje,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 6.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                                )
                            }
                            Text(
                                text = "Muéstraselo al encargado cuando vayas a recoger tu premio.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Nuevo saldo
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.GeneratingTokens, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
                            Text(
                                text = "Saldo restante: ${data.nuevoSaldo} pts",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Botón cerrar
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("¡Listo!", style = MaterialTheme.typography.labelLarge)
                        }

                        Text(
                            text = "Se cerrará automáticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
//  Utilidades
// ============================================================
private fun formatFecha(fechaIso: String): String {
    return try {
        val instant = java.time.Instant.parse(fechaIso)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (_: Exception) {
        fechaIso.take(10)
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
