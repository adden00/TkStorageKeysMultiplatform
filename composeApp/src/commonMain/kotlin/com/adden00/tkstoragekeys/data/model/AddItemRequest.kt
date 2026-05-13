package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddItemRequest(
    val newItem: EquipDto,
    val keyholderName: String
)
