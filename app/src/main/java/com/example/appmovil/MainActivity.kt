package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.appmovil.ui.navigation.AppNavigation
import com.example.appmovil.ui.theme.EcoCycleTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EcoCycleTheme {
                Surface {
                    AppNavigation(
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}