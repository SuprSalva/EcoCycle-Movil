package com.example.appmovil.network.dto

data class NotificacionResponse(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val icono: String,
    val leida: Boolean,
    val fecha: String
)
