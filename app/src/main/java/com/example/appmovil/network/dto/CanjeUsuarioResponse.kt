package com.example.appmovil.network.dto

data class CanjeUsuarioResponse(
    val id: String,
    val codigoCanje: String,
    val recompensaId: String,
    val recompensaNombre: String,
    val recompensaImagenUrl: String?,
    val puntosUsados: Double,
    val fecha: String,
    val reclamado: Boolean,
    val fechaReclamado: String?
)
