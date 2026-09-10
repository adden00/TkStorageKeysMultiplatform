package com.adden00.tkstoragekeys.features.person_details_screen.mvi

sealed class PersonDetailsScreenEffect {
    data class ShowToast(val message: String) : PersonDetailsScreenEffect()
}
