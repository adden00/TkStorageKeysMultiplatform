package com.adden00.tkstoragekeys.features.people_search_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

data class PeopleSearchScreenState(
    val isSearching: Boolean = false,
    val isReturning: Boolean = false,
    val currentEquipList: List<EquipItem> = listOf(),
    val enteredSearchText: String = "",
    /** Запрос, по которому получен текущий список: поле ввода очищается, а обновлять выдачу надо. */
    val lastQuery: String = "",
)

fun PeopleSearchScreenState.isBusy() = isSearching || isReturning
