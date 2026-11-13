package com.adden00.tkstoragekeys.features.person_details_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class PersonDetailsScreenEffect {
    data class ShowToast(val message: String) : PersonDetailsScreenEffect()
    data class OpenItem(val item: EquipItem) : PersonDetailsScreenEffect()
}
