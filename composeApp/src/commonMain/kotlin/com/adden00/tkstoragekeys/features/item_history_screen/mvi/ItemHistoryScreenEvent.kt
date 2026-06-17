package com.adden00.tkstoragekeys.features.item_history_screen.mvi

sealed class ItemHistoryScreenEvent {
    data class LoadHistory(val itemId: String) : ItemHistoryScreenEvent()
}
