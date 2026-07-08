package com.example.appmovil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.appmovil.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import com.example.appmovil.network.ApiClient
import com.example.appmovil.network.dto.ActualizarPerfilRequest
import com.example.appmovil.network.dto.UsuarioResponse
import com.example.appmovil.ui.components.GlobalLoader
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onLogout: () -> Unit,
    onAjustesClick: () -> Unit,
    onAyudaClick: () -> Unit,
    onTerminosClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var perfil by remember { mutableStateOf<UsuarioResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    fun loadPerfil() {
        coroutineScope.launch {
            GlobalLoader.show("Cargando perfil...")
            try {
                val response = ApiClient.apiService.getPerfil()
                if (response.isSuccessful) {
                    perfil = response.body()?.data
                } else {
                    errorMessage = "Error al cargar perfil."
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión."
            } finally {
                GlobalLoader.hide()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadPerfil()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EcoCycle", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id = R.drawable.logo_transparent), contentDescription = "Logo", modifier = Modifier.size(28.dp), tint = Color.Unspecified)
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
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Profile Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (perfil?.avatarUrl.isNullOrEmpty()) {
                                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    AsyncImage(
                                        model = perfil?.avatarUrl,
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${perfil?.nombre} ${perfil?.apellidos}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = perfil?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Menu Options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuOptionItem(
                        icon = Icons.Filled.Settings,
                        title = "Ajustes",
                        onClick = onAjustesClick
                    )
                    MenuOptionItem(
                        icon = Icons.Filled.Help,
                        title = "Ayuda y Soporte",
                        onClick = onAyudaClick
                    )
                    MenuOptionItem(
                        icon = Icons.Filled.Description,
                        title = "Términos y Condiciones",
                        onClick = onTerminosClick
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Cerrar Sesión")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    if (showEditDialog && perfil != null) {
        var nombre by remember { mutableStateOf(perfil!!.nombre ?: "") }
        var apellidos by remember { mutableStateOf(perfil!!.apellidos ?: "") }
        var telefono by remember { mutableStateOf(perfil!!.telefono ?: "") }
        var direccion by remember { mutableStateOf(perfil!!.direccion ?: "") }
        var avatarUrl by remember { mutableStateOf(perfil!!.avatarUrl) }
        var localImageUri by remember { mutableStateOf<Uri?>(null) }
        var isSaving by remember { mutableStateOf(false) }

        val predefinedAvatars = listOf(
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Felix",
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Aneka",
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Jack",
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Jocelyn",
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Nala",
            "https://api.dicebear.com/9.x/avataaars/svg?seed=Destiny"
        )

        val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                localImageUri = uri
                avatarUrl = null
            }
        }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showEditDialog = false },
            title = { Text("Editar Perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (localImageUri != null) {
                                AsyncImage(model = localImageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else if (!avatarUrl.isNullOrEmpty()) {
                                AsyncImage(model = avatarUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                            }
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.3f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }

                    Text("O elige un ícono:", style = MaterialTheme.typography.labelMedium)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.height(100.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(predefinedAvatars) { url ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(if (avatarUrl == url) 2.dp else 0.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .clickable { 
                                        avatarUrl = url
                                        localImageUri = null
                                    }
                            ) {
                                AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            GlobalLoader.show("Guardando...")
                            isSaving = true
                            try {
                                var finalAvatarUrl = avatarUrl
                                if (localImageUri != null) {
                                    val storageRef = FirebaseStorage.getInstance().reference.child("fotosperfil/${perfil!!.id}_${UUID.randomUUID()}")
                                    val uploadTask = storageRef.putFile(localImageUri!!).await()
                                    finalAvatarUrl = storageRef.downloadUrl.await().toString()
                                }
                                val req = ActualizarPerfilRequest(nombre, apellidos, telefono, direccion, finalAvatarUrl)
                                val response = ApiClient.apiService.updatePerfil(req)
                                if (response.isSuccessful) {
                                    showEditDialog = false
                                    loadPerfil() // Reload to get updated data
                                } else {
                                    // Optional: handle error
                                }
                            } catch (e: Exception) {
                                // Optional: handle exception
                            } finally {
                                GlobalLoader.hide()
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }, enabled = !isSaving) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MenuOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
