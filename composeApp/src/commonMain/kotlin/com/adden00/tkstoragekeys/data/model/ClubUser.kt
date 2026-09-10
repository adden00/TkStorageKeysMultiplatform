package com.adden00.tkstoragekeys.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Служебные записи справочника с фиксированными id. */
const val WAREHOUSE_ID = "warehouse"

data class ClubUserShort(
    val id: String,
    val fullName: String,
    val birthDate: String,
    val telegram: String,
)

data class ClubUser(
    val id: String,
    val fullName: String,
    val birthDate: String,
    val intake: String,
    val phone: String,
    val email: String,
    val telegram: String,
    val vk: String,
    val joinYear: String,
    val tourTraining: String,
    val rank: String,
    val rankExtra: String,
    val experience: String,
)

data class UserSearchResult(
    val users: List<ClubUserShort>,
    val hasMore: Boolean,
    val message: String?,
)

@Serializable
data class UserShortDto(
    @SerialName("id") val id: String = "",
    @SerialName("fullName") val fullName: String = "",
    @SerialName("birthDate") val birthDate: String = "",
    @SerialName("telegram") val telegram: String = "",
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String = "",
    @SerialName("fullName") val fullName: String = "",
    @SerialName("birthDate") val birthDate: String = "",
    @SerialName("intake") val intake: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("telegram") val telegram: String = "",
    @SerialName("vk") val vk: String = "",
    @SerialName("joinYear") val joinYear: String = "",
    @SerialName("tourTraining") val tourTraining: String = "",
    @SerialName("rank") val rank: String = "",
    @SerialName("rankExtra") val rankExtra: String = "",
    @SerialName("experience") val experience: String = "",
)

@Serializable
data class UserSearchResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("users") val users: List<UserShortDto> = listOf(),
    @SerialName("hasMore") val hasMore: Boolean = false,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class UserResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("user") val user: UserDto? = null,
)

@Serializable
data class UsersImportResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("importedCount") val importedCount: Int? = null,
    @SerialName("message") val message: String? = null,
)

fun UserShortDto.toClubUserShort() = ClubUserShort(id, fullName, birthDate, telegram)

fun UserDto.toClubUser() = ClubUser(
    id, fullName, birthDate, intake, phone, email, telegram, vk, joinYear, tourTraining, rank, rankExtra, experience
)
