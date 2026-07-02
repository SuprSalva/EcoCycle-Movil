package com.example.appmovil.network

import com.example.appmovil.network.dto.ApiResponse
import com.example.appmovil.network.dto.RegistroRequest
import com.example.appmovil.network.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EcoCycleApi {

    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<ApiResponse<Any>>

    @GET("api/usuario/perfil")
    suspend fun getPerfil(): Response<ApiResponse<UsuarioResponse>>

    @GET("api/recompensa")
    suspend fun getRecompensas(): Response<ApiResponse<List<com.example.appmovil.network.dto.RecompensaResponse>>>

    @POST("api/recompensa/canjear")
    suspend fun canjearRecompensa(@Body request: com.example.appmovil.network.dto.CanjearRequest): Response<ApiResponse<Any>>

    @GET("api/recompensa/mis-canjes")
    suspend fun getMisCanjes(): Response<ApiResponse<List<com.example.appmovil.network.dto.CanjeUsuarioResponse>>>

    @GET("api/usuario/historial")
    suspend fun getHistorial(): Response<ApiResponse<List<com.example.appmovil.network.dto.HistorialItemResponse>>>

    @PUT("api/usuario/perfil")
    suspend fun updatePerfil(@Body request: com.example.appmovil.network.dto.ActualizarPerfilRequest): Response<ApiResponse<Any>>

    @GET("api/notificacion")
    suspend fun getNotificaciones(): Response<ApiResponse<List<com.example.appmovil.network.dto.NotificacionResponse>>>

    @PUT("api/notificacion/leer")
    suspend fun marcarNotificacionesLeidas(): Response<ApiResponse<Any>>
}
