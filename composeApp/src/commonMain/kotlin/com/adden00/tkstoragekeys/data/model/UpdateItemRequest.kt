package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateItemRequest(
    val newItem: EquipDto,
    val keyholderName: String
)
