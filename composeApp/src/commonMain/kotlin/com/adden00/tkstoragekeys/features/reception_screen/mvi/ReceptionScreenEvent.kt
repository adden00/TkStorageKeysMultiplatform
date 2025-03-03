package com.adden00.tkstoragekeys.features.reception_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class ReceptionScreenEvent {
    data class GetInfo(val id: String) : ReceptionScreenEvent()
    data class UpdateInfo(val id: String, val newItem: EquipItem, val updateType: UpdateType) : ReceptionScreenEvent()
    data class OnSearchTextChanged(val text: String) : ReceptionScreenEvent()
    data class OnLocationTextChanged(val text: String) : ReceptionScreenEvent()
    data class OnEventTextChanged(val text: String) : ReceptionScreenEvent()
    data class UpdateEquipItem(val item: EquipItem) : ReceptionScreenEvent()
    data object DismissNotExistsDialog : ReceptionScreenEvent()
}

enum class UpdateType {
    MOVING,
    RETURNING,
    MOVING_NO_NEW_STORAGE
}