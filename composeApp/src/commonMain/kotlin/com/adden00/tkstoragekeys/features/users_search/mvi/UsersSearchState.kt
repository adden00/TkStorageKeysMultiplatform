package com.adden00.tkstoragekeys.features.users_search.mvi

import com.adden00.tkstoragekeys.data.model.ClubUserShort

data class UsersSearchState(
    val query: String = "",
    val users: List<ClubUserShort> = emptyList(),
    val isLoading: Boolean = false,
    // выдача соответствует текущему запросу — только тогда пустой список значит "никого нет"
    val isSearched: Boolean = false,
    // подсказка сервера ("уточните запрос") или ошибка
    val hint: String? = null,
)
