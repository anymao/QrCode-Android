package com.anymore.qrcode.core.util

import android.util.Log

/**
 * Created by anymore on 2023/4/1.
 */
object Logger {

    const val VERBOSE = Log.VERBOSE
    const val DEBUG = Log.DEBUG
    const val WARN = Log.WARN
    const val ERROR = Log.ERROR
    const val NONE = ERROR + 10

    var level = VERBOSE

    fun v(tag: String, message: String) {
        if (level <= VERBOSE) {
            Log.v(tag, message)
        }
    }

    fun d(tag: String, message: String) {
        if (level <= DEBUG) {
            Log.d(tag, message)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (level <= WARN) {
            Log.w(tag, message, throwable)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (level <= ERROR) {
            Log.e(tag, message, throwable)
        }
    }


}