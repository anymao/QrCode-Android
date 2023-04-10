package com.anymore.qrcode.core

import android.Manifest
import android.app.Service
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anymore.auto.ServiceLoader
import com.anymore.qrcode.core.util.Logger
import com.anymore.qrcode.core.view.ViewFinderView
import java.util.concurrent.Executors

/**
 * Created by anymore on 2023/2/23.
 */
class QrCodeScanActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "QrCodeScanActivity"
        private const val REQUEST_CODE = 10241
        const val EXTRA_OPTION = "QrCodeScanActivity.EXTRA_OPTION"
    }

    private lateinit var previewView: PreviewView
    private lateinit var viewFinder: ViewFinderView
    private lateinit var soundPool: SoundPool
    private var soundId: Int = -1
    private var ringReady = false
    private var implAlias: String = ""
    private var session: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scan)
        @Suppress("DEPRECATION")
        val option = intent?.getSerializableExtra(EXTRA_OPTION) as? ScanOption
        if (option != null) {
            implAlias = option.implAlias
            session = option.session
            Logger.d(TAG, "session:$session,使用:$implAlias")
        }
        previewView = findViewById(R.id.preview_view)
        viewFinder = findViewById(R.id.view_finder)
        soundId = initSoundPool()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initCamara()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE
            )
        }
    }

    override fun onDestroy() {
        if (!session.isNullOrEmpty()) {
            ScanManager.unregister(session)
        }
        soundPool.release()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val granted = grantResults.none { it != PackageManager.PERMISSION_GRANTED }
            if (granted) {
                initCamara()
            } else {
                Toast.makeText(this, "必须授予相机权限才能使用此功能", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun initCamara() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = future.get()
            // Preview
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val executor = Executors.newSingleThreadExecutor {
                Thread(it, "qrcode-scan-analysis")
            }
            val loader =
                ServiceLoader.load<BaseAnalyzer>(implAlias)
            val analyzer = loader.requireFirstPriority()
            analyzer.register {
                Logger.d(TAG, "scan:$it")
                runOnMainThread {
                    stopCamera(cameraProvider)
                    doScanCompleted()
                    ScanManager.getHandler(session).handle(this, it)
                }
            }
            imageAnalysis.setAnalyzer(executor, analyzer)

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

            } catch (e: Exception) {
                Logger.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun stopCamera(provider: ProcessCameraProvider) {
        Logger.d(TAG,"stopCamera")
        try {
            provider.unbindAll()
        } catch (e: Exception) {
            Logger.e(TAG, "Use case binding failed", e)
        }
    }

    private fun initSoundPool(): Int {
        soundPool = SoundPool.Builder()
            .setMaxStreams(1).build()
        val id = soundPool.load(this, R.raw.qrcode_completed, 1)
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (id == sampleId && status == 0) {
                ringReady = true
            }
        }
        return id
    }

    private fun doScanCompleted() {
        if (ringReady && soundId > 0) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
        vibrate()
        viewFinder.stopAnimation()
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val service =
                getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as? VibratorManager ?: return
            service.defaultVibrator
        } else {
            val service = getSystemService(Service.VIBRATOR_SERVICE) as? Vibrator ?: return
            service
        }
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(345L)
        }
    }

    private fun runOnMainThread(runnable: Runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            runnable.run()
        } else {
            ContextCompat.getMainExecutor(this).execute(runnable)
        }
    }
}