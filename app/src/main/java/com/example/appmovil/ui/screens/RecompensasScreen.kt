package com.example.appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class RecompensaMock(val titulo: String, val puntos: Int, val icon: ImageVector)

@Composable
fun RecompensasScreen() {
    val recompensas = listOf(
        RecompensaMock("Café Gratis", 500, Icons.Filled.Coffee),
        RecompensaMock("Descuento 10%", 1000, Icons.Filled.Discount),
        RecompensaMock("Transporte", 1500, Icons.Filled.DirectionsBus),
        RecompensaMock("Cine 2x1", 2000, Icons.Filled.Movie),
        RecompensaMock("Camiseta Eco", 3000, Icons.Filled.Checkroom),
        RecompensaMock("Suscripción", 5000, Icons.Filled.Star)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "Catálogo de Recompensas",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Canjea tus puntos por increíbles premios ecológicos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recompensas) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().aspectRatio(0.85f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(item.icon, contentDescription = item.titulo, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.titulo, 
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${item.puntos} pts", 
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Canjear")
                        }
                    }
                }
            }
        }
    }
}
