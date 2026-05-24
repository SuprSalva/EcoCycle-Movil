package com.example.appmovil.network.dto

data class HistorialItemResponse(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val puntos: String,
    val esPositivo: Boolean,
    val fecha: String
)
