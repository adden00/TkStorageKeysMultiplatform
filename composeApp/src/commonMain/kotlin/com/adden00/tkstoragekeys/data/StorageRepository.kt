package com.adden00.tkstoragekeys.data

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.toEquipItem
import com.adden00.tkstoragekeys.data.network.StorageApiService

class StorageRepository(
    private val api: StorageApiService
) {

    suspend fun getItemFromTable(id: String): EquipItem {
        val item = api.getItem(id)
        if (!item.success || item.equipItem == null) {
            throw EquipNotFoundException
        } else return item.equipItem.toEquipItem()
    }

    suspend fun updateItem(id: String, item: EquipItem): EquipItem {
        val response = api.updateItem(
            id,
            item.id,
            item.category,
            item.brand,
            item.name,
            item.color,
            item.weigh,
            item.quality,
            item.location,
            item.event,
            item.info,
            item.date
        )
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException
        } else return response.equipItem.toEquipItem()
    }

    suspend fun addItem(id: String, item: EquipItem): EquipItem {
        val response = api.addItem(
            id,
            item.id,
            item.category,
            item.brand,
            item.name,
            item.color,
            item.weigh,
            item.quality,
            item.location,
            item.event,
            item.info,
            item.date
        )
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException
        } else return response.equipItem.toEquipItem()
    }

    suspend fun getFreeId(): String {
        val response = api.getFreeId()
        if (!response.success || response.id == null) {
            throw EquipNotFoundException
        } else return response.id
    }
}

object EquipNotFoundException: RuntimeException("Cнаряжение не найдено")