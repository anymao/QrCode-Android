package com.anymore.qrcode.mlkit

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.anymore.qrcode.core.util.Logger
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

    companion object {
        private const val TAG = "MlkitAnalyzer"
    }

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
        val mediaImage = image.image
        if (mediaImage != null) {
            val start = System.currentTimeMillis()
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val task = scanner.process(inputImage)
            task.addOnSuccessListener {
                if (!it.isNullOrEmpty()) {
                    callback?.accept(it.first().rawValue)
                    Logger.d("MlkitAnalyzer", it.first().rawValue.orEmpty())
                    Logger.v(TAG, "解码耗时:${System.currentTimeMillis() - start} ms")
                }
            }.addOnFailureListener {
                Logger.e(TAG, "扫码中出现错误", it)
            }.addOnCompleteListener {
                image.close()
            }
        } else {
            image.close()
        }
    }
}