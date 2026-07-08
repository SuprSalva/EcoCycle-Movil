package com.example.appmovil.network.dto

import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    val id: String?,
    val nombre: String?,
    val apellidos: String?,
    val telefono: String?,
    val email: String?,
    val direccion: String?,
    @SerializedName("saldoPuntos") val puntosDisponibles: Double?,
    val totalBotellasRecicladas: Int?,
    val nivelActual: String?,
    val metaActual: Int?,
    val faltantesSiguienteNivel: Int?,
    val rol: String?,
    val avatarUrl: String?
)
