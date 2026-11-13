package com.adden00.tkstoragekeys.features.person_details_screen.mvi

import com.adden00.tkstoragekeys.data.model.ClubUser
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.features.history_feed.HistoryFeedState

data class PersonDetailsScreenState(
    val isLoading: Boolean = false,
    val user: ClubUser? = null,
    val items: List<EquipItem> = emptyList(),
    val isReturning: Boolean = false,
    // id мог смениться после обновления справочника из таблицы
    val isNotFound: Boolean = false,
    val history: HistoryFeedState = HistoryFeedState(),
    val openingItemId: String? = null,
)

fun PersonDetailsScreenState.isBusy() = isLoading || isReturning
