package com.anymore.qrcode.core.result

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by anymore on 2023/3/31.
 */
@Parcelize
data class Area(
    val topLeft: PointF,
    val topRight: PointF,
    val bottomLeft: PointF,
    val bottomRight: PointF
) : Parcelable
