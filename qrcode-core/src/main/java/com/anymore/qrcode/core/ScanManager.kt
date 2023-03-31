package com.anymore.qrcode.core

/**
 * Created by anymore on 2023/3/3.
 */

object ScanManager {
    private val handlers: MutableMap<String, ScanHandler> = HashMap()

    var defaultHandler: ScanHandler = ScanHandler.EMPTY

    internal fun register(session: String, handler: ScanHandler) {
        handlers[session] = handler
    }

    internal fun getHandler(session: String): ScanHandler {
        return handlers[session] ?: defaultHandler
    }

    internal fun unregister(session: String) {
        handlers.remove(session)
    }

}