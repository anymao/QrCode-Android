package com.anymore.qrcode.wechat.scanner

import org.opencv.core.Mat
import org.opencv.utils.Converters

class WeChatScanner {

    companion object {
        // Used to load the 'scanner' library on application startup.
        init {
            System.loadLibrary("scanner")
        }
    }
    
    val nativeObjAddr: Long

    private constructor(addr: Long) {
        nativeObjAddr = addr
    }

    constructor(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ) {
        nativeObjAddr = WeChatQRCode(
            detector_prototxt_path,
            detector_caffe_model_path,
            super_resolution_prototxt_path,
            super_resolution_caffe_model_path
        )
    }

    fun detectAndDecode(img: Mat, points: List<Mat>): List<String> {
        val points_mat = Mat()
        val retVal = detectAndDecode(
            nativeObjAddr, img.nativeObj, points_mat.nativeObjAddr
        )
        Converters.Mat_to_vector_Mat(points_mat, points)
        points_mat.release()
        return retVal
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        delete(nativeObjAddr)
    }

    external fun WeChatQRCode(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ): Long


    external fun detectAndDecode(
        nativeObj: Long,
        img_nativeObj: Long,
        points_mat_nativeObj: Long
    ): List<String>


    external fun delete(nativeObj: Long)

}
