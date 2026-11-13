package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("keyholderName") val keyholderName: String,
    @SerialName("key") val key: String,
)

@Serializable
data class LoginResponse(
    @SerialName("success") val success: Boolean = false,
    @SerialName("token") val token: String? = null,
    @SerialName("keyholderName") val keyholderName: String? = null,
    @SerialName("message") val message: String? = null,
)
