package com.anymore.qrcode.core

import java.io.Serializable
import java.util.*

/**
 * Created by anymore on 2023/3/3.
 */
class ScanOption private constructor(
    val session: String,
    val implAlias: String
) : Serializable {


    class Builder {
        private val session: String = UUID.randomUUID().toString()
        var implAlias: String = ""
        var handler: ScanHandler? = null
        fun build(): ScanOption {
            handler?.run {
                ScanManager.register(session, this)
            }
            return ScanOption(session, implAlias)
        }
    }
}