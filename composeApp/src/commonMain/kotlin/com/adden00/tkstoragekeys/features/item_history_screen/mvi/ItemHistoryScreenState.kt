package com.adden00.tkstoragekeys.features.item_history_screen.mvi

import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry

data class ItemHistoryScreenState(
    val isLoading: Boolean = false,
    val entries: List<ItemHistoryEntry> = emptyList(),
    val error: String = "",
)
