package com.adden00.tkstoragekeys.data

import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import com.adden00.tkstoragekeys.data.model.toEquipItem
import com.adden00.tkstoragekeys.data.model.toItemHistoryEntry
import com.adden00.tkstoragekeys.data.network.StorageApi

class StorageRepository(
    private val api: StorageApi,
    private val appSettings: AppSettings
) {

    suspend fun getItemFromTable(id: String): EquipItem {
        val response = api.getItem(id)
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException(response.message)
        } else return response.equipItem.toEquipItem()
    }

    suspend fun searchByLocation(query: String): List<EquipItem> {
        val response = api.searchByLocation(query.trim())
        return response.items.map { it.toEquipItem() }
    }

    suspend fun searchByName(query: String): List<EquipItem> {
        val response = api.searchByName(query.trim())
        return response.items.map { it.toEquipItem() }
    }

    suspend fun updateItem(id: String, item: EquipItem): EquipItem {
        val response = api.updateItem(
            appSettings.keyHolderName,
            id,
            item
        )
        if (!response.success || response.equipItem == null) {
            throw EquipNotFoundException(response.message)
        } else return response.equipItem.toEquipItem()
    }

    suspend fun addItem(item: EquipItem): EquipItem {
        val response = api.addItem(
            appSettings.keyHolderName,
            item
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

    suspend fun getItemHistory(id: String): List<ItemHistoryEntry> {
        val response = api.getItemHistory(id)
        return response.entries.map { it.toItemHistoryEntry() }
    }

    suspend fun exportItems(format: ExportFormat): ByteArray = api.exportItems(format)
}

class EquipNotFoundException(override val message: String? = null) : RuntimeException(message)
