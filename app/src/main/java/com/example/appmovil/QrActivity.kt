package com.example.appmovil

import android.Manifest
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.util.concurrent.Executors

class QrActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private var scanningEnabled = true
    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_CODE = 100
    private var escaneando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false


        barcodeView = findViewById(R.id.barcodeScanner)


        if (checkCameraPermission()) {
            iniciarScanner()

        if (allPermissionsGranted()) {
            startCamera()
        } else {


            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )

        }

    }

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(viewFinder.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(analysisExecutor, QrCodeAnalyzer()) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (_: Exception) { }
        }

    }

    inner class QrCodeAnalyzer : ImageAnalysis.Analyzer {
        private val scanner = BarcodeScanning.getClient()

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage: Image? = imageProxy.image
            if (mediaImage == null || !scanningEnabled) {
                imageProxy.close()
                return
            }

            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes: List<Barcode> ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue ?: continue
                        if (rawValue.startsWith("machine_")) {
                            scanningEnabled = false
                            onQrScanned(rawValue)
                            break
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun onQrScanned(sessionId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            runOnUiThread {
                Toast.makeText(this, "Debes iniciar sesión primero", Toast.LENGTH_LONG).show()
                finish()
            }
            return
        }

        val db = FirebaseDatabase.getInstance()
        val sessionRef = db.getReference("sessions").child(sessionId)

        val updates = mapOf(
            "linked" to true,
            "userId" to user.uid,
            "userEmail" to (user.email ?: ""),
            "linkedAt" to System.currentTimeMillis()
        )

        sessionRef.updateChildren(updates)
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "Máquina vinculada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this, "Error al vincular: ${e.message}", Toast.LENGTH_LONG).show()
                    scanningEnabled = true
                }
            }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
