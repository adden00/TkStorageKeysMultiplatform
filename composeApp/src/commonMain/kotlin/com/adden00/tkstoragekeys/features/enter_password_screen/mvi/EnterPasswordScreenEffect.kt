package com.adden00.tkstoragekeys.features.enter_password_screen.mvi

sealed class EnterPasswordScreenEffect {
    data object OpenMain : EnterPasswordScreenEffect()
    data class ShowToast(val message: String) : EnterPasswordScreenEffect()
}
