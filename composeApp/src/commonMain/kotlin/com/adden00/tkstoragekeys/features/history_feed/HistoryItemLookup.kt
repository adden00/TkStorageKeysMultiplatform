package com.adden00.tkstoragekeys.features.history_feed

import com.adden00.tkstoragekeys.data.EquipNotFoundException
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.data.model.EquipItem
import kotlinx.coroutines.CancellationException

sealed class HistoryItemLookup {
    data class Found(val item: EquipItem) : HistoryItemLookup()
    data class Failed(val message: String) : HistoryItemLookup()
}

suspend fun StorageRepository.lookupHistoryItem(itemId: String): HistoryItemLookup = try {
    HistoryItemLookup.Found(getItemFromTable(itemId))
} catch (e: CancellationException) {
    throw e
} catch (e: EquipNotFoundException) {
    HistoryItemLookup.Failed(e.message ?: "$itemId не найдено")
} catch (e: Exception) {
    HistoryItemLookup.Failed("Exception! ${e.message ?: "no message"}")
}
