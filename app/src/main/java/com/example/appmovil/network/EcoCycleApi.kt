package com.example.appmovil.network

import com.example.appmovil.network.dto.ApiResponse
import com.example.appmovil.network.dto.RegistroRequest
import com.example.appmovil.network.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EcoCycleApi {

    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<ApiResponse<Any>>

    @GET("api/usuario/perfil")
    suspend fun getPerfil(): Response<ApiResponse<UsuarioResponse>>
}
