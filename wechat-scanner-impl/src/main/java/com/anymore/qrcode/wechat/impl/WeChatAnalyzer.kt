package com.anymore.qrcode.wechat.impl

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.anymore.qrcode.wechat.scanner.WeChatScanner
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.File

/**
 * Created by anymore on 2023/2/26.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "wechat-scanner")
class WeChatAnalyzer:BaseAnalyzer {

    private var callback:Consumer<String>? = null

    private val scanner:WeChatScanner

    init {
        val context = requireNotNull(WeChatScannerInitializer.appContext){"WeChatScanner init failed!"}
        val dir = File(context.filesDir,"wechat-scanner-model")
        scanner = WeChatScanner(File(dir,WeChatScannerInitializer.detect_pro).absolutePath,File(dir,WeChatScannerInitializer.detect_model).absolutePath,File(dir,WeChatScannerInitializer.sr_pro).absolutePath,File(dir,WeChatScannerInitializer.sr_model).absolutePath)
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }

    override fun analyze(image: ImageProxy) {
        Log.d("WeChatAnalyzer", "analyze")
        val result = scanner.detectAndDecode(gray(image),ArrayList())
        if (!result.isNullOrEmpty()){
            Log.d("WeChatAnalyzer",result.first())
            callback?.accept(result.first())
        }
        image.close()
    }

    private fun gray(image: ImageProxy): Mat {
        val planeProxy = image.planes
        val width = image.width
        val height = image.height
        val yPlane = planeProxy[0].buffer
        val yPlaneStep = planeProxy[0].rowStride
        return Mat(height, width, CvType.CV_8UC1, yPlane, yPlaneStep.toLong())
    }
}