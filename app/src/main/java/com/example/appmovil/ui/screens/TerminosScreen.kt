package com.example.appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminosScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Términos de Servicio",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "1. Aceptación de los Términos\nAl acceder y utilizar la aplicación EcoCycle, usted acepta cumplir y estar sujeto a estos Términos de Servicio y a nuestra Política de Privacidad.\n\n" +
                       "2. Uso de la Aplicación\nLa aplicación EcoCycle tiene como objetivo incentivar el reciclaje mediante la asignación de puntos por materiales reciclados. El uso de la app es personal e intransferible.\n\n" +
                       "3. Puntos y Recompensas\nLos puntos obtenidos mediante el reciclaje no tienen valor monetario real y solo pueden ser canjeados por recompensas ofrecidas dentro de la app. Nos reservamos el derecho de modificar el valor de los puntos o retirar recompensas en cualquier momento.\n\n" +
                       "4. Privacidad\nSu privacidad es importante para nosotros. Recopilamos datos mínimos necesarios para el funcionamiento de la app, como su nombre e historial de reciclaje. Consulte nuestra Política de Privacidad completa para más detalles.\n\n" +
                       "5. Conducta del Usuario\nQueda prohibido cualquier intento de falsificar reciclajes o abusar del sistema de recompensas. Cualquier actividad sospechosa resultará en la suspensión inmediata de la cuenta.\n\n" +
                       "6. Cambios en los Términos\nNos reservamos el derecho de actualizar o modificar estos términos en cualquier momento sin previo aviso. Es su responsabilidad revisar periódicamente estos términos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Última actualización: 24 de Mayo de 2026",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
