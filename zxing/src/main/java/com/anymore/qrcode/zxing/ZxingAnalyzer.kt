package com.anymore.qrcode.zxing

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.addAll
import kotlin.collections.forEachIndexed
import kotlin.collections.set


/**
 * Created by anymore on 2023/2/24.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "zxing")
class ZxingAnalyzer : BaseAnalyzer {

    companion object {
        private const val TAG = "ZxingAnalyzer"
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        private fun ByteArray.toIntArray(): IntArray {
            val res = IntArray(size)
            forEachIndexed { index, byte ->
                res[index] = byte.toInt() and 0xFF
            }
            return res
        }
    }


    private val decoder = MultiFormatReader()
    private var callback: Consumer<String>? = null

    init {

        // 解码的参数
        val hints: HashMap<DecodeHintType, Any> = HashMap(2)
        // 可以解析的编码类型
        val decodeFormats: Vector<BarcodeFormat> = Vector()
        if (decodeFormats.isEmpty()) {
            decodeFormats.addAll(BarcodeFormat.values())
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        decoder.setHints(hints)
    }

    override fun register(success: Consumer<String>) {
        callback = success
    }


    override fun analyze(image: ImageProxy) {
        try {
            val sourceBitmap = image.toBitmap()
            val w = sourceBitmap.width
            val h = sourceBitmap.height
            val pixels = IntArray(w * h)
            sourceBitmap.getPixels(pixels,0,w,0,0,w,h)
            sourceBitmap.recycle()
            val source = RGBLuminanceSource(w, h, pixels)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val result = decoder.decode(bitmap)
            if (!result.text.isNullOrEmpty()) {
                callback?.accept(result.text)
            }
            Log.d(TAG, result.text)
        } catch (e: Throwable) {
            if (e is NotFoundException) {
                Log.d(TAG, "照片中没有发现二维码")
            } else {
                Log.e(TAG, "扫码中出现错误:$e")
            }
        } finally {
            image.close()
        }
    }
}