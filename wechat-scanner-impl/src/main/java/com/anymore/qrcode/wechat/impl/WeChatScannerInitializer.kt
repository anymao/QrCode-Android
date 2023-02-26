package com.anymore.qrcode.wechat.impl

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.RestrictTo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by anymore on 2023/2/26.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class WeChatScannerInitializer : ContentProvider() {

    companion object{
        const val TAG = "WeChatScannerInit"
        const val detect_model = "detect.caffemodel"
        const val sr_model = "sr.caffemodel"
        const val detect_pro =  "detect.prototxt"
        const val sr_pro = "sr.prototxt"
        val files = arrayOf(detect_model, sr_model, detect_pro, sr_pro)
        @SuppressLint("StaticFieldLeak")
        internal var appContext:Context? = null
    }
    override fun onCreate(): Boolean {
        doInitWeChatScanner(context)
        appContext = context
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    @OptIn(DelicateCoroutinesApi::class)
    private fun doInitWeChatScanner(context: Context?){
        if (context == null){
            Log.e(TAG,"context == null init failed!")
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG,Thread.currentThread().name)
            val dir = File(context.filesDir,"wechat-scanner-model")
            if (!dir.exists()){
                if (!dir.mkdirs()){
                    Log.e(TAG,"创建目录失败!")
                    return@launch
                }
                files.forEach {
                    val file = File(dir,it)
                    if (!file.exists() || file.length() == 0L){
                        file.outputStream().use {os->
                            context.assets.open(it).use {`is`->
                                `is`.copyTo(os)
                            }
                        }
                    }
                }
            }
            Log.d(TAG,"初始化模型成功")
        }
    }
}