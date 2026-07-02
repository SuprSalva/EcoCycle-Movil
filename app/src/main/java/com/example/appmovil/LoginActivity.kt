package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.appmovil.ui.components.GlobalLoaderOverlay
import com.example.appmovil.ui.screens.AuthScreen
import com.example.appmovil.ui.theme.EcoCycleTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Si ya hay una sesión activa, saltar al MainActivity directamente
        if (FirebaseAuth.getInstance().currentUser != null) {
            goToMain()
            return
        }

        setContent {
            EcoCycleTheme {
                Surface {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AuthScreen(
                            onLoginSuccess = {
                                goToMain()
                            }
                        )
                        GlobalLoaderOverlay()
                    }
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
