package com.adden00.tkstoragekeys.features.reception_screen.mvi

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.features.users_search.LocationPick

data class ReceptionScreenState(
    val isSearching: Boolean = false,
    val isMovingToPerson: Boolean = false,
    val isReturning: Boolean = false,
    val isMovingToNewStorage: Boolean = false,
    val currentEquipItem: EquipItem? = null,
    val isNumberNoxExistsShown: Boolean = false,
    val error: String = "",
    val enteredSearchText: String = "",
    // не сбрасывается после выдачи: несколько вещей подряд одному человеку
    val selectedLocation: LocationPick? = null,
    val enteredEventText: String = "",
    val notFoundedId: String? = null,
    val exportingFormat: ExportFormat? = null,
    val isExportingToSheets: Boolean = false,
    val isImportingUsers: Boolean = false,
)
