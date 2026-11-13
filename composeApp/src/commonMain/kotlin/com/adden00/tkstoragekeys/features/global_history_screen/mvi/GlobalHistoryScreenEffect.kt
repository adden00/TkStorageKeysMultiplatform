package com.adden00.tkstoragekeys.features.global_history_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class GlobalHistoryScreenEffect {
    data class OpenItem(val item: EquipItem) : GlobalHistoryScreenEffect()
    data class ShowToast(val message: String) : GlobalHistoryScreenEffect()
}
