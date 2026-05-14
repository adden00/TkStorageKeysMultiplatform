package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.EquipsResponse
import com.adden00.tkstoragekeys.data.model.IdResponse

interface StorageApi {

    suspend fun getItem(id: String): EquipResponse

    suspend fun getAllItems(): EquipsResponse

    suspend fun searchByLocation(query: String): EquipsResponse

    suspend fun searchByName(query: String): EquipsResponse

    suspend fun getFreeId(): IdResponse

    suspend fun updateItem(keyholderName: String, id: String, item: EquipItem): EquipResponse

    suspend fun addItem(keyholderName: String, item: EquipItem): EquipResponse

}
