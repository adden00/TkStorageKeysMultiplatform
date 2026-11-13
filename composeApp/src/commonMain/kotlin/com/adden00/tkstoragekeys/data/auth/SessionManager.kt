package com.adden00.tkstoragekeys.data.auth

import com.adden00.tkstoragekeys.data.local.AppSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager(private val appSettings: AppSettings) {

    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val expired: SharedFlow<Unit> = _expired.asSharedFlow()

    val token: String get() = appSettings.authToken

    fun onUnauthorized() {
        if (appSettings.authToken.isEmpty()) return
        appSettings.clearSession()
        _expired.tryEmit(Unit)
    }
}
