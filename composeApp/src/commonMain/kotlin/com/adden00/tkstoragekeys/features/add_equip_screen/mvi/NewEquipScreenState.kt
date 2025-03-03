package com.adden00.tkstoragekeys.features.add_equip_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

data class NewEquipScreenState(
    val isAdding: Boolean = false,
    val isReceivingId: Boolean = false,
    val isStartDataFilled: Boolean = false,
    val updatingItemId: String = "",
    val enteredItem: EquipItem = EquipItem()
)
