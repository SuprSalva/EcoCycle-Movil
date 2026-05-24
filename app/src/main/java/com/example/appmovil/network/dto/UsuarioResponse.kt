package com.example.appmovil.network.dto

data class UsuarioResponse(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val telefono: String,
    val email: String,
    val direccion: String?,
    val saldoPuntos: Double,
    val rol: String
)
