package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemHistoryResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("entries") val entries: List<ItemHistoryEntryDto> = emptyList(),
)

@Serializable
data class ItemHistoryEntryDto(
    @SerialName("action") val action: String = "",
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
    @SerialName("timestamp") val timestamp: String = "",
    @SerialName("keyholderName") val keyholderName: String = "",
)

fun ItemHistoryEntryDto.toItemHistoryEntry() = ItemHistoryEntry(
    action = action,
    id = id,
    category = category,
    brand = brand,
    name = name,
    color = color,
    weigh = weigh,
    quality = quality,
    location = location,
    event = event,
    info = info,
    timestamp = timestamp,
    keyholderName = keyholderName,
)
