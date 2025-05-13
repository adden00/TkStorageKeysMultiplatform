package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("equipItem") val equipItem: EquipDto? = null,
    @SerialName("message") val message: String? = null,

    )

@Serializable
data class EquipDto(
    @SerialName("id") val id: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("brand") val brand: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("color") val color: String = "",
    @SerialName("weigh") val weigh: String = "",
    @SerialName("quality") val quality: String = "",
    @SerialName("location") val location: String = "",
    @SerialName("event") val event: String = "",
    @SerialName("info") val info: String = "",
    @SerialName("date") val date: String = "",
)

fun EquipDto.toEquipItem() = EquipItem(
    id, category, brand, name, color, weigh, quality.extractQuality(), location, event, info, date
)

fun EquipItem.toDto() = EquipDto(
    id, category, brand, name, color, weigh, quality?.value.orEmpty(), location, event, info, date
)