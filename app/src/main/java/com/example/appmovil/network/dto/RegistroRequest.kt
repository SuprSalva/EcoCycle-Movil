package com.example.appmovil.network.dto

data class RegistroRequest(
    val nombre: String,
    val apellidos: String,
    val telefono: String,
    val email: String,
    val password: String,
    val direccion: String? = null,
    val id: String? = null
)
