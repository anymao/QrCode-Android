package com.anymore.qrcode.wechat.impl

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.anymore.qrcode.wechat.scanner.WeChatScanner
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by anymore on 2023/2/26.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "wechat-scanner")
class WeChatAnalyzer : BaseAnalyzer {

    companion object {
        private const val TAG = "WeChatAnalyzer"

        @Volatile
        private var initialized = false
        private const val detect_model = "detect.caffemodel"
        private const val sr_model = "sr.caffemodel"
        private const val detect_pro = "detect.prototxt"
        private const val sr_pro = "sr.prototxt"
        private val files = arrayOf(detect_model, sr_model, detect_pro, sr_pro)
        private val fileMap: MutableMap<String, String> = ConcurrentHashMap(4)

        fun init(context: Context) {
            if (initialized && fileMap.size == files.size) {
                Log.d(TAG, "WeChatAnalyzer 已经初始化完成")
                return
            }
            checkModelFiles(context)
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun checkModelFiles(context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                val startTime = System.currentTimeMillis()
                Log.d(TAG, "开始检查模型文件#" + Thread.currentThread().name)
                val dir = File(context.filesDir, "wechat-scanner-model")
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "创建模型目录失败!")
                        return@launch
                    }
                }
                files.forEach {
                    val file = File(dir, it)
                    if (!file.exists() || file.length() == 0L) {
                        Log.d(TAG, "模型文件<$it>需要复制")
                        file.outputStream().use { os ->
                            context.assets.open(it).use { `is` ->
                                `is`.copyTo(os)
                            }
                        }
                    }
                    fileMap[it] = file.absolutePath
                }
                Log.d(TAG, "初始化模型成功，耗时:${System.currentTimeMillis() - startTime} ms")
                initialized = true
            }
        }
    }

    private var callback: Consumer<String>? = null

    private val scanner: WeChatScanner

    init {
        if (fileMap.size != files.size) {
            throw IllegalStateException("请先调用WeChatAnalyzer.init(context)进行初始化")
        }
        scanner = WeChatScanner(
            requireNotNull(fileMap[detect_pro]),
            requireNotNull(fileMap[detect_pro]),
            requireNotNull(fileMap[sr_pro]),
            requireNotNull(fileMap[sr_model])
        )
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }

    override fun analyze(image: ImageProxy) {
        val result = scanner.detectAndDecode(gray(image), ArrayList())
        if (!result.isNullOrEmpty()) {
            Log.d(TAG, result.first())
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