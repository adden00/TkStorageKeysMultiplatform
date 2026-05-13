package com.adden00.tkstoragekeys.features.people_search_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

sealed class PeopleSearchScreenEvent {
    data class GetInfo(val query: String) : PeopleSearchScreenEvent()
    data class ReturnItem(val item: EquipItem) : PeopleSearchScreenEvent()
    data class OnSearchTextChanged(val text: String) : PeopleSearchScreenEvent()
    data object DismissNotExistsDialog : PeopleSearchScreenEvent()
}
