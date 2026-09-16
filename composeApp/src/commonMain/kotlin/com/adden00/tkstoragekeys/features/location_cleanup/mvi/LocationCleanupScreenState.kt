package com.adden00.tkstoragekeys.features.location_cleanup.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem

/**
 * Группа вещей с одинаковым текстом местоположения. Разбирать имеет смысл именно
 * группами: 161 предмет — это всего 47 разных текстов.
 */
data class LocationGroup(
    val location: String,
    val items: List<EquipItem>,
) {
    val count: Int get() = items.size
}

data class LocationCleanupScreenState(
    val isLoading: Boolean = false,
    val groups: List<LocationGroup> = emptyList(),
    /** Текст группы, которую сейчас применяем, и прогресс по ней. */
    val applyingLocation: String? = null,
    val applyingDone: Int = 0,
    val applyingTotal: Int = 0,
    val error: String = "",
) {
    val totalItems: Int get() = groups.sumOf { it.count }
}
