package com.adden00.tkstoragekeys.features.item_history_screen.mvi

sealed class ItemHistoryScreenEffect {
    data class ShowToast(val message: String) : ItemHistoryScreenEffect()
}
