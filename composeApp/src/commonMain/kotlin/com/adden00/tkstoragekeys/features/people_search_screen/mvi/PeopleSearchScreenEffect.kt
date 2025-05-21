package com.adden00.tkstoragekeys.features.people_search_screen.mvi

sealed class PeopleSearchScreenEffect {
    data class ShowToast(val message: String) : PeopleSearchScreenEffect()
}
