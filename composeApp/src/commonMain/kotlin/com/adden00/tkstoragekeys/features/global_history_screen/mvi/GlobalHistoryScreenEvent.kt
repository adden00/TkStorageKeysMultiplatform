package com.adden00.tkstoragekeys.features.global_history_screen.mvi

sealed class GlobalHistoryScreenEvent {
    data object LoadIfEmpty : GlobalHistoryScreenEvent()
    data object Refresh : GlobalHistoryScreenEvent()
    data object LoadMore : GlobalHistoryScreenEvent()
    data object Retry : GlobalHistoryScreenEvent()
    data class OpenItem(val itemId: String) : GlobalHistoryScreenEvent()
}
