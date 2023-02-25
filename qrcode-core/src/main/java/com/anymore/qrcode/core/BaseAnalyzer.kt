package com.anymore.qrcode.core

import androidx.camera.core.ImageAnalysis
import androidx.core.util.Consumer

/**
 * Created by anymore on 2023/2/24.
 */
interface BaseAnalyzer : ImageAnalysis.Analyzer {
    fun register(success: Consumer<String>)
}