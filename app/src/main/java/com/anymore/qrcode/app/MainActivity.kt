package com.anymore.qrcode.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anymore.qrcode.core.QrCodeScanActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_camera).setOnClickListener {
            startActivity(Intent(this,QrCodeScanActivity::class.java))
        }
    }
}