package com.adden00.tkstoragekeys.features.add_equip_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class NewEquipScreenEffect {
    data class ShowToast(val message: String) : NewEquipScreenEffect()
    data class NavigateBack(val addedItem: EquipItem) : NewEquipScreenEffect()
}