package com.adden00.tkstoragekeys.data

import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.toEquipItem
import com.adden00.tkstoragekeys.data.network.StorageApiService

class StorageRepository(
    private val api: StorageApiService,
    private val appSettings: AppSettings
) {

    suspend fun getItemFromTable(id: String): EquipItem {
        val response = api.getItem(id)
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException(response.message)
        } else return response.equipItem.toEquipItem()
    }

    suspend fun updateItem(id: String, item: EquipItem): EquipItem {
        val response = api.updateItem(
            appSettings.keyHolderName,
            id,
            item.id,
            item.category,
            item.brand,
            item.name,
            item.color,
            item.weigh,
            item.quality?.value.orEmpty(),
            item.location,
            item.event,
            item.info,
            item.date,
        )
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException(response.message)
        } else return response.equipItem.toEquipItem()
    }

    suspend fun addItem(id: String, item: EquipItem): EquipItem {
        val response = api.addItem(
            appSettings.keyHolderName,
            id,
            item.id,
            item.category,
            item.brand,
            item.name,
            item.color,
            item.weigh,
            item.quality?.value.orEmpty(),
            item.location,
            item.event,
            item.info,
            item.date
        )
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException(response.message)
        } else return response.equipItem.toEquipItem()
    }

    suspend fun getFreeId(): String {
        val response = api.getFreeId()
        if (!response.success || response.id == null) {
            throw EquipNotFoundException(response.message)
        } else return response.id
    }
}

class EquipNotFoundException(override val message: String? = null) : RuntimeException(message)