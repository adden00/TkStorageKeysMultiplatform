package com.adden00.tkstoragekeys.features.add_equip_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class NewEquipScreenEvent {
    data object AddEquipItem : NewEquipScreenEvent()
    data class UpdateEquipItem(val id: String) : NewEquipScreenEvent()
    data object GetFreeId : NewEquipScreenEvent()
    data class OnEnteredItemChange(val item: EquipItem) : NewEquipScreenEvent()
    data class FillStartFields(val editingItemId: String, val item: EquipItem) : NewEquipScreenEvent()
}