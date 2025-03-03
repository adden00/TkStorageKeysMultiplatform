package com.adden00.tkstoragekeys.features.reception_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

data class ReceptionScreenState(
    val isSearching: Boolean = false,
    val isMovingToPerson: Boolean = false,
    val isReturning: Boolean = false,
    val isMovingToNewStorage: Boolean = false,
    val currentEquipItem: EquipItem? = null,
    val isNumberNoxExistsShown: Boolean = false,
    val error: String = "",
    val enteredSearchText: String = "",
    val enteredLocationText: String = "",
    val enteredEventText: String = "",
    val notFoundedId: String? = null,
)
