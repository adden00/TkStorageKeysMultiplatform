package com.adden00.tkstoragekeys.features.people_search_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

data class PeopleSearchScreenState(
    val isSearching: Boolean = false,
    val isReturning: Boolean = false,
    val currentEquipList: List<EquipItem> = listOf(),
    val isNumberNoxExistsShown: Boolean = false,
    val enteredSearchText: String = "",
)

fun PeopleSearchScreenState.isBusy() = isSearching || isReturning
