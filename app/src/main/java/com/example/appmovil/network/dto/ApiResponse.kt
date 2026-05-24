package com.example.appmovil.network.dto

data class ApiResponse<T>(
    val suceso: Boolean,
    val message: String,
    val data: T?,
    val errors: List<String>?
)
