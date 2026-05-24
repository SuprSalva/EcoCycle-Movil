package com.example.appmovil.network.dto

data class RecompensaResponse(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val costoPuntos: Double,
    val stock: Int,
    val activa: Boolean
)
