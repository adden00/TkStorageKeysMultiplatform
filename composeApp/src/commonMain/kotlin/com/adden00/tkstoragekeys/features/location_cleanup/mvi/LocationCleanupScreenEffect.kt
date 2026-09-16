package com.adden00.tkstoragekeys.features.location_cleanup.mvi

sealed class LocationCleanupScreenEffect {
    data class ShowToast(val message: String) : LocationCleanupScreenEffect()
}
