package com.anymore.qrcode.mlkit

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Created by anymore on 2023/2/25.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "ml-kit")
class MlkitAnalyzer : BaseAnalyzer {

    private val scanner: BarcodeScanner
    private var callback: Consumer<String>? = null

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        Log.d("MlkitAnalyzer", "analyze")
        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val task = scanner.process(inputImage)
            task.addOnSuccessListener {
                if (!it.isNullOrEmpty()) {
                    callback?.accept(it.first().rawValue)
                    Log.d("MlkitAnalyzer", it.first().rawValue.orEmpty())
                }
            }.addOnCompleteListener {
                image.close()
            }
        } else {
            image.close()
        }
    }
}