package com.anymore.qrcode.wechat.scanner

import org.opencv.core.Mat
import org.opencv.utils.Converters

class WeChatScanner(
    detector_prototxt_path: String,
    detector_caffe_model_path: String,
    super_resolution_prototxt_path: String,
    super_resolution_caffe_model_path: String
) {

    companion object {
        // Used to load the 'scanner' library on application startup.
        init {
            System.loadLibrary("wechat_scanner")
        }
    }
    
    private val nativeObj: Long

    init {
        nativeObj = initModels(
            detector_prototxt_path,
            detector_caffe_model_path,
            super_resolution_prototxt_path,
            super_resolution_caffe_model_path
        )
    }

    fun detectAndDecode(img: Mat, points: List<Mat>): List<String>? {
        val points_mat = Mat()
        val retVal = detectAndDecode(
            nativeObj, img.nativeObj, points_mat.nativeObjAddr
        )
        Converters.Mat_to_vector_Mat(points_mat, points)
        points_mat.release()
        return retVal
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        delete(nativeObj)
    }

    private external fun initModels(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ): Long


    external fun detectAndDecode(
        nativeObj: Long,
        img_nativeObj: Long,
        points_mat_nativeObj: Long
    ): List<String>?


    private external fun delete(nativeObj: Long)

}
