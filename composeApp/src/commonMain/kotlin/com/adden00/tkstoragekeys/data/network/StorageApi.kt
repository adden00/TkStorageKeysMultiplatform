package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.EquipsResponse
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ExportSheetsResponse
import com.adden00.tkstoragekeys.data.model.IdResponse
import com.adden00.tkstoragekeys.data.model.ItemHistoryResponse
import com.adden00.tkstoragekeys.data.model.UserResponse
import com.adden00.tkstoragekeys.data.model.UserSearchResponse
import com.adden00.tkstoragekeys.data.model.UsersImportResponse

interface StorageApi {

    suspend fun getItem(id: String): EquipResponse

    suspend fun getAllItems(): EquipsResponse

    suspend fun search(query: String): EquipsResponse

    suspend fun getFreeId(): IdResponse

    suspend fun updateItem(keyholderName: String, id: String, item: EquipItem, historyAction: String): EquipResponse

    suspend fun addItem(keyholderName: String, item: EquipItem): EquipResponse

    suspend fun getItemHistory(id: String): ItemHistoryResponse

    suspend fun exportItems(format: ExportFormat): ByteArray

    suspend fun exportToSheets(): ExportSheetsResponse

    suspend fun searchUsers(query: String): UserSearchResponse

    suspend fun getUser(id: String): UserResponse

    suspend fun getUserItems(id: String): EquipsResponse

    suspend fun importUsers(): UsersImportResponse

}
