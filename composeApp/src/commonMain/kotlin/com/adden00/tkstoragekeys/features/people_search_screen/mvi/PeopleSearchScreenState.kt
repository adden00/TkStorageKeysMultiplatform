package com.adden00.tkstoragekeys.features.people_search_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

enum class SearchMode { BY_LOCATION, BY_NAME }

data class PeopleSearchScreenState(
    val isSearching: Boolean = false,
    val isReturning: Boolean = false,
    val currentEquipList: List<EquipItem> = listOf(),
    val enteredSearchText: String = "",
    val searchMode: SearchMode = SearchMode.BY_LOCATION,
)

fun PeopleSearchScreenState.isBusy() = isSearching || isReturning
