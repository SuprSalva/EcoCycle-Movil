package com.example.appmovil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import com.journeyapps.barcodescanner.DecoratedBarcodeView


class QrActivity : AppCompatActivity() {


    private lateinit var barcodeView: DecoratedBarcodeView

    private val CAMERA_PERMISSION_CODE = 100

    private var escaneando = false



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



        if(requestCode == CAMERA_PERMISSION_CODE){


            if(
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ){

                iniciarScanner()

            }else{


                Toast.makeText(
                    this,
                    "Se necesita permiso de cámara",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }





    private fun iniciarScanner(){


        barcodeView.decodeContinuous { result ->


            if(escaneando){
                return@decodeContinuous
            }



            val sessionId = result.text



            if(!sessionId.isNullOrEmpty()){


                escaneando = true


                Log.d(
                    "QR_SCAN",
                    "QR leído: $sessionId"
                )


                vincularMaquina(sessionId)


            }


        }


    }






    private fun vincularMaquina(sessionId:String){



        val usuarioActual =
            FirebaseAuth.getInstance()
                .currentUser




        if(usuarioActual == null){



            Toast.makeText(
                this,
                "No hay usuario iniciado en Firebase",
                Toast.LENGTH_LONG
            ).show()



            escaneando = false

            return

        }





        val usuarioId = usuarioActual.uid



        Log.d(
            "FIREBASE_AUTH",
            "Usuario UID: $usuarioId"
        )





        val datos = hashMapOf(

            "usuario_id" to usuarioId,

            "estado" to "activa",

            "fecha" to FieldValue.serverTimestamp()

        )





        FirebaseFirestore.getInstance()

            .collection("sesiones_reciclaje")

            .document(sessionId)

            .set(
                datos,
                SetOptions.merge()
            )


            .addOnSuccessListener {



                Log.d(
                    "FIRESTORE",
                    "Documento creado correctamente: $sessionId"
                )



                Toast.makeText(
                    this,
                    "Máquina vinculada correctamente",
                    Toast.LENGTH_LONG
                ).show()



                finish()

            }



            .addOnFailureListener { error ->



                Log.e(
                    "FIRESTORE_ERROR",
                    error.message ?: "Error desconocido"
                )



                error.printStackTrace()



                Toast.makeText(
                    this,
                    "Error Firestore: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()



                escaneando = false

            }


    }






    override fun onResume(){

        super.onResume()


        if(::barcodeView.isInitialized){

            barcodeView.resume()

        }

    }






    override fun onPause(){

        super.onPause()
        if(::barcodeView.isInitialized){
            barcodeView.pause()
        }
    }
}