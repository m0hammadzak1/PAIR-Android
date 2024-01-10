package com.sova.pair.model

/**
 * Created by Zaki on 09-01-2024.
 */
data class SavedRouter(
    var ipAddress: String = "192.168.1.2",
    var macAddress: String = "AA:BB:CC:DD:EE:FF",
    var isOnline: Boolean = true,
    var internet: Boolean = true
)