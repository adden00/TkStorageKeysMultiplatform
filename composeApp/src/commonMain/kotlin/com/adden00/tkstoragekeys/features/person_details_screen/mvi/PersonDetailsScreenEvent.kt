package com.adden00.tkstoragekeys.features.person_details_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class PersonDetailsScreenEvent {
    data class Load(val userId: String) : PersonDetailsScreenEvent()
    data class ReturnItem(val userId: String, val item: EquipItem) : PersonDetailsScreenEvent()
    data object LoadMoreHistory : PersonDetailsScreenEvent()
    data object RetryHistory : PersonDetailsScreenEvent()
    data class OpenHistoryItem(val itemId: String) : PersonDetailsScreenEvent()
}
