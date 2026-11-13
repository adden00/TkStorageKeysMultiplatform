package com.adden00.tkstoragekeys.features.enter_password_screen.mvi

sealed class EnterPasswordScreenEvent {
    data object CheckSession : EnterPasswordScreenEvent()
    data class Login(val keyholderName: String, val key: String) : EnterPasswordScreenEvent()
}
