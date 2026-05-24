package com.example.appmovil.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val task = user.getIdToken(false)
                val tokenResult = Tasks.await(task)
                val token = tokenResult.token
                
                if (token != null) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        return chain.proceed(request)
    }
}
