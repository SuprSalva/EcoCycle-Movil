package com.example.appmovil

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        
        findViewById<Button>(R.id.btnClose).setOnClickListener {
            finish()
        }
    }
}
