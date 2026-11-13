package com.adden00.tkstoragekeys.features.global_history_screen.mvi

import com.adden00.tkstoragekeys.features.history_feed.HistoryFeedState

data class GlobalHistoryScreenState(
    val history: HistoryFeedState = HistoryFeedState(),
    val openingItemId: String? = null,
)
