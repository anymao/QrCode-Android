package com.anymore.qrcode.wechat

class NativeLib {

    /**
     * A native method that is implemented by the 'wechat' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'wechat' library on application startup.
        init {
            System.loadLibrary("wechat")
        }
    }
}