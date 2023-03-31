package com.anymore.qrcode.core

import androidx.fragment.app.FragmentActivity

/**
 * Created by anymore on 2023/4/10.
 */
fun interface ScanHandler {
    fun handle(activity: FragmentActivity, result: String)

    companion object {
        val EMPTY: ScanHandler = ScanHandler { _, _ -> }
    }
}