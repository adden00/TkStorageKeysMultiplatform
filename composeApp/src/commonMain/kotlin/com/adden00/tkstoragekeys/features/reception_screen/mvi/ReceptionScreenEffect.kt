package com.adden00.tkstoragekeys.features.reception_screen.mvi

sealed class ReceptionScreenEffect {
    data class ShowToast(val message: String): ReceptionScreenEffect()
    data class SaveFile(val bytes: ByteArray, val extension: String) : ReceptionScreenEffect()
}