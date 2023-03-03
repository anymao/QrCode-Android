package com.anymore.qrcode.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anymore.qrcode.core.QrCodeScanActivity
import com.anymore.qrcode.core.ScanOption

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_1).setOnClickListener {
            val option = ScanOption.Builder().run {
                implAlias = "zxing"
                handler = { activity, text ->
                    Toast.makeText(activity, "[zxing:]$text", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "[zxing:]$text")
                }
                build()
            }
            val intent = Intent(this, QrCodeScanActivity::class.java)
            intent.putExtra(QrCodeScanActivity.EXTRA_OPTION, option)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_2).setOnClickListener {
            val option = ScanOption.Builder().run {
                implAlias = "ml-kit"
                handler = { activity, text ->
                    Toast.makeText(activity, "[ml-kit:]$text", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "[ml-kit:]$text")
                }
                build()
            }
            val intent = Intent(this, QrCodeScanActivity::class.java)
            intent.putExtra(QrCodeScanActivity.EXTRA_OPTION, option)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_3).setOnClickListener {
            val option = ScanOption.Builder().run {
                implAlias = "hms"
                handler = { activity, text ->
                    Toast.makeText(activity, "[hms:]$text", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "[hms:]$text")
                }
                build()
            }
            val intent = Intent(this, QrCodeScanActivity::class.java)
            intent.putExtra(QrCodeScanActivity.EXTRA_OPTION, option)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_4).setOnClickListener {
            val option = ScanOption.Builder().run {
                implAlias = "wechat-scanner"
                handler = { activity, text ->
                    Toast.makeText(activity, "[wechat-scanner:]$text", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "[wechat-scanner:]$text")
                }
                build()
            }
            val intent = Intent(this, QrCodeScanActivity::class.java)
            intent.putExtra(QrCodeScanActivity.EXTRA_OPTION, option)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_5).setOnClickListener {
            val option = ScanOption.Builder().run {
                handler = { activity, text ->
                    Toast.makeText(activity, "[default:]$text", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "[default:]$text")
                }
                build()
            }
            val intent = Intent(this, QrCodeScanActivity::class.java)
            intent.putExtra(QrCodeScanActivity.EXTRA_OPTION, option)
            startActivity(intent)
        }
    }
}