package com.adden00.tkstoragekeys.features.location_cleanup.mvi

import com.adden00.tkstoragekeys.features.location_cleanup.mvi.LocationGroup as Group

sealed class LocationCleanupScreenEvent {
    data object Load : LocationCleanupScreenEvent()

    /** Привязать всю группу к выбранной записи справочника. */
    data class BindGroup(val group: Group, val userId: String, val userName: String) :
        LocationCleanupScreenEvent()

    /** Убрать группу из списка до следующего открытия экрана, ничего не меняя. */
    data class SkipGroup(val group: Group) : LocationCleanupScreenEvent()
}
