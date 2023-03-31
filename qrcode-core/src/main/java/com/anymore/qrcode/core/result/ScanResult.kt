package com.anymore.qrcode.core.result

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by anymore on 2023/3/31.
 */
@Parcelize
class ScanResult(val bitmap: Bitmap, val text: String, val areas: List<Area>? = null) : Parcelable