package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.appmovil.ui.components.GlobalLoaderOverlay
import com.example.appmovil.ui.navigation.AppNavigation
import com.example.appmovil.ui.theme.EcoCycleTheme
import com.google.firebase.auth.FirebaseAuth

import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)

        setContent {
            EcoCycleTheme {
                Surface {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavigation(
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                finish()
                            }
                        )
                        GlobalLoaderOverlay()
                    }
                }
            }
        }
    }
}