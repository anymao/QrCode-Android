package com.anymore.qrcode.hms

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzer
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.mlsdk.common.MLFrame

/**
 * Created by anymore on 2023/2/25.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "hms")
class HmsAnalyzer : BaseAnalyzer {

    companion object {
        private const val TAG = "HmsAnalyzer"
    }

    private val decoder: HmsScanAnalyzer
    private var callback: Consumer<String>? = null

    init {
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和DataMatrix的码
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).create()
        decoder = HmsScanAnalyzer(options)
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }

    override fun analyze(image: ImageProxy) {
        val bitmap = image.toBitmap()
        val task = decoder.analyzInAsyn(MLFrame.fromBitmap(bitmap))
        task.addOnSuccessListener {
            if (!it.isNullOrEmpty()) {
                callback?.accept(it.first().originalValue)
            }
        }.addOnFailureListener {
            Log.e(TAG, "扫码中出现错误", it)
        }.addOnCompleteListener {
            image.close()
        }
    }
}