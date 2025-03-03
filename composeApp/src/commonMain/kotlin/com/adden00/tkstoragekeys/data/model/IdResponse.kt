package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("id") val id: String? = null
)
