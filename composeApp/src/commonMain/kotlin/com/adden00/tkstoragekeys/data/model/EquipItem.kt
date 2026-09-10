package com.adden00.tkstoragekeys.data.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.storage

data class EquipItem(
    val id: String = "",
    val category: String = "",
    val brand: String = "",
    val name: String = "",
    val color: String = "",
    val weigh: String = "",
    val quality: Quality? = null,
    val location: String = "",
    val event: String = "",
    val info: String = "",
    val date: String = "",
    val locationUserId: String? = null,
)

// сервер отдаёт канонический "Склад", а старые записи могут быть "склад" — сравниваем без регистра
@Composable
fun EquipItem.isOnStorage() =
    locationUserId == WAREHOUSE_ID || location.trim().equals(stringResource(Res.string.storage), ignoreCase = true)

fun EquipItem.isWritingOff() =
    quality == Quality.WRITE_OFF || quality == Quality.TO_WRITE_OFF
