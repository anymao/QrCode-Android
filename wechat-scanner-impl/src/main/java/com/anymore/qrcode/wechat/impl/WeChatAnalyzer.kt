package com.anymore.qrcode.wechat.impl

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.anymore.qrcode.core.util.Logger
import com.anymore.qrcode.wechat.scanner.WeChatScanner
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.opencv.android.Utils
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
                Logger.d(TAG, "WeChatAnalyzer 已经初始化完成")
                return
            }
            checkModelFiles(context)
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun checkModelFiles(context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                val startTime = System.currentTimeMillis()
                Logger.d(TAG, "开始检查模型文件#" + Thread.currentThread().name)
                val dir = File(context.filesDir, "wechat-scanner-model")
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Logger.e(TAG, "创建模型目录失败!")
                        return@launch
                    }
                }
                files.forEach {
                    val file = File(dir, it)
                    if (!file.exists() || file.length() == 0L) {
                        Logger.d(TAG, "模型文件<$it>需要复制")
                        file.outputStream().use { os ->
                            context.assets.open(it).use { `is` ->
                                `is`.copyTo(os)
                            }
                        }
                    }
                    fileMap[it] = file.absolutePath
                }
                Logger.d(TAG, "初始化模型成功，耗时:${System.currentTimeMillis() - startTime} ms")
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
            requireNotNull(fileMap[detect_model]),
            requireNotNull(fileMap[sr_pro]),
            requireNotNull(fileMap[sr_model])
        )
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }

    override fun analyze(image: ImageProxy) {
        try {
            val start = System.currentTimeMillis()
            val mat = Mat()
            Utils.bitmapToMat(image.toBitmap(), mat)
            val result = scanner.detectAndDecode(mat, ArrayList())
            if (!result.isNullOrEmpty()) {
                Logger.d(TAG, result.first())
                callback?.accept(result.first())
                Logger.v(TAG, "解码耗时:${System.currentTimeMillis() - start} ms")
            }
        } catch (e: Throwable) {
            Logger.e(TAG, "扫码中出现错误", e)
        } finally {
            image.close()
        }
    }

}