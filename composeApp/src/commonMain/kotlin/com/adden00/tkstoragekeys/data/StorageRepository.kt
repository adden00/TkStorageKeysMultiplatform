package com.adden00.tkstoragekeys.data

import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.model.ClubUser
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import com.adden00.tkstoragekeys.data.model.UserSearchResult
import com.adden00.tkstoragekeys.data.model.toClubUser
import com.adden00.tkstoragekeys.data.model.toClubUserShort
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

    suspend fun search(query: String): List<EquipItem> {
        val response = api.search(query.trim())
        return response.items.map { it.toEquipItem() }
    }

    suspend fun updateItem(id: String, item: EquipItem, historyAction: String = "ОБНОВЛЕНО"): EquipItem {
        val response = api.updateItem(
            keyholderName = appSettings.keyHolderName,
            id = id,
            item = item,
            historyAction = historyAction
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

    suspend fun exportToSheets() {
        val response = api.exportToSheets()
        if (!response.success) throw EquipNotFoundException(response.message)
    }

    suspend fun searchUsers(query: String): UserSearchResult {
        val response = api.searchUsers(query.trim())
        if (!response.success) throw EquipNotFoundException(response.message)
        return UserSearchResult(
            users = response.users.map { it.toClubUserShort() },
            hasMore = response.hasMore,
            message = response.message
        )
    }

    suspend fun getUser(id: String): ClubUser {
        val response = api.getUser(id)
        if (!response.success || response.user == null) {
            throw EquipNotFoundException()
        } else return response.user.toClubUser()
    }

    suspend fun getUserItems(id: String): List<EquipItem> {
        val response = api.getUserItems(id)
        if (!response.success) throw EquipNotFoundException(response.message)
        return response.items.map { it.toEquipItem() }
    }

    /** @return текст для пользователя об итогах импорта */
    suspend fun importUsers(): String {
        val response = api.importUsers()
        if (!response.success) throw EquipNotFoundException(response.message)
        return listOfNotNull(
            response.importedCount?.let { "Импортировано записей: $it" },
            response.message
        ).joinToString(". ").ifEmpty { "Справочник обновлён" }
    }
}

class EquipNotFoundException(override val message: String? = null) : RuntimeException(message)
