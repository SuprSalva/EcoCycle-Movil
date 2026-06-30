package com.example.appmovil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class QrActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView

    private val CAMERA_PERMISSION_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)


        barcodeView = findViewById(R.id.barcodeScanner)


        if (checkCameraPermission()) {

            iniciarScanner()

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }


    private fun checkCameraPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )


        if (requestCode == CAMERA_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                iniciarScanner()

            } else {

                Toast.makeText(
                    this,
                    "Se necesita permiso de cámara",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }


    private fun iniciarScanner() {

        barcodeView.decodeContinuous { result ->

            val texto = result.text


            if (!texto.isNullOrEmpty()) {

                val ref =
                    FirebaseDatabase.getInstance()
                        .getReference("sessions")
                        .child(texto)
                        .child("linked")


                ref.setValue(true)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Máquina vinculada",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(
                            this,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()

                    }


                finish()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }


    override fun onPause() {
        super.onPause()

        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
    }
}