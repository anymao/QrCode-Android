package com.anymore.qrcode.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anymore.auto.ServiceLoader
import java.util.concurrent.Executors

/**
 * Created by anymore on 2023/2/23.
 */
class QrCodeScanActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "QrCodeScanActivity"
        private const val REQUEST_CODE = 10241
    }

    private lateinit var previewView: PreviewView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scan)
        previewView = findViewById(R.id.preview_view)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initCamara()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val granted = grantResults.none { it != PackageManager.PERMISSION_GRANTED }
            if (granted) {
                initCamara()
            }
        }
    }

    private fun initCamara() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = future.get()
            // Preview
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val executor = Executors.newSingleThreadExecutor {
                Thread(it,"qrcode-scan-analysis")
            }
            val analyzer = ServiceLoader.load<BaseAnalyzer>("hms").requireFirstPriority()
            imageAnalysis.setAnalyzer(executor,analyzer)

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageAnalysis)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }
}