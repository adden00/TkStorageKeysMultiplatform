package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExportSheetsResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
)
