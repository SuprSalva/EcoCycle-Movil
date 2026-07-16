package com.example.appmovil.network

import com.example.appmovil.EcoCycleConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val BASE_URL = "http://${EcoCycleConfig.SERVER_HOST}:${EcoCycleConfig.NET_API_PORT}/"

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
