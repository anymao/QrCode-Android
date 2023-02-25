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


/**
 * Created by anymore on 2023/2/24.
 */
@AutoService(value = [BaseAnalyzer::class], alias = "zxing")
class ZxingAnalyzer : BaseAnalyzer {


    private val decoder = MultiFormatReader()
    private var callback: Consumer<String>? = null

    companion object {
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        private fun ByteArray.toIntArray():IntArray{
            val res = IntArray(size)
            forEachIndexed { index, byte ->
                res[index] = byte.toInt() and 0xFF
            }
            return res
        }
    }

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
        Log.d("ZxingAnalyzer", "analyze")
        val source = RGBLuminanceSource(
            image.width,
            image.height,
            image.planes[0].buffer.toByteArray().toIntArray())
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = decoder.decode(bitmap)
            callback?.accept(result.text)
            Log.d("ZxingAnalyzer", result.text)
        }catch (e:Throwable){
//            Log.e("ZxingAnalyzer","error:",e)
        }finally {
            image.close()
        }
    }
}