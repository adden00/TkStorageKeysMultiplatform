package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.EquipsResponse
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ExportSheetsResponse
import com.adden00.tkstoragekeys.data.model.IdResponse
import com.adden00.tkstoragekeys.data.model.ItemHistoryResponse

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

}
