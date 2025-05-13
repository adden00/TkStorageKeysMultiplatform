package com.adden00.tkstoragekeys.data.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.new_storage

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
)

@Composable
fun EquipItem.isOnStorage() =
    location == stringResource(Res.string.new_storage)

fun EquipItem.isWritingOff() =
    quality == Quality.WRITE_OFF || quality == Quality.TO_WRITE_OFF
