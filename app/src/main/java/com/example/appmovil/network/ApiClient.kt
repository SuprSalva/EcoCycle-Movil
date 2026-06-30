package com.example.appmovil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Reemplaza por el puerto en el que corre tu API .NET
    private const val BASE_URL = "http://192.168.1.4:5000/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: EcoCycleApi = retrofit.create(EcoCycleApi::class.java)
}
