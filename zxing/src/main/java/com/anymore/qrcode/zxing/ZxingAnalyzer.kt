package com.anymore.qrcode.zxing

import androidx.camera.core.ImageProxy
import androidx.core.util.Consumer
import com.anymore.auto.AutoService
import com.anymore.qrcode.core.BaseAnalyzer
import com.anymore.qrcode.core.util.Logger
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import java.util.*
import kotlin.collections.set


/**
 * Created by anymore on 2023/2/24.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "zxing")
class ZxingAnalyzer : BaseAnalyzer {

    companion object {
        private const val TAG = "ZxingAnalyzer"

    }


    private val decoder = MultiFormatReader()
    private var callback: Consumer<String>? = null

    init {

        // 解码的参数
        val hints: HashMap<DecodeHintType, Any> = HashMap(2)
        // 可以解析的编码类型
        val decodeFormats: Vector<BarcodeFormat> = Vector<BarcodeFormat>().apply {
            add(BarcodeFormat.QR_CODE)
            add(BarcodeFormat.AZTEC)
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
            val start = System.currentTimeMillis()
            val sourceBitmap = image.toBitmap()
            val w = sourceBitmap.width
            val h = sourceBitmap.height
            val pixels = IntArray(w * h)
            sourceBitmap.getPixels(pixels,0,w,0,0,w,h)
            sourceBitmap.recycle()
            val source = RGBLuminanceSource(w, h, pixels)
            val bitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
            val result = decoder.decode(bitmap)
            if (!result.text.isNullOrEmpty()) {
                callback?.accept(result.text)
            }
            Logger.v(TAG,"解码耗时:${System.currentTimeMillis()-start} ms")
        } catch (e: Throwable) {
            if (e is NotFoundException) {
//                Log.d(TAG, "照片中没有发现二维码")
            } else {
                Logger.e(TAG, "扫码中出现错误",e)
            }
        } finally {
            image.close()
        }
    }
}