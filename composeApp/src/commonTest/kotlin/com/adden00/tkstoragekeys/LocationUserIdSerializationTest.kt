package com.adden00.tkstoragekeys

import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.UpdateItemRequest
import com.adden00.tkstoragekeys.data.model.WAREHOUSE_ID
import com.adden00.tkstoragekeys.data.model.toDto
import com.adden00.tkstoragekeys.data.model.toEquipItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** Сервер различает "поле не прислано" и "прислана пустая строка" — проверяем, что клиент их не путает. */
class LocationUserIdSerializationTest {

    // та же конфигурация, что у HttpClient в CommonModule
    private val json = Json { ignoreUnknownKeys = true }

    private fun newItemJson(item: EquipItem) =
        json.parseToJsonElement(
            json.encodeToString(UpdateItemRequest(newItem = item.toDto(), keyholderName = "k", historyAction = "ОБНОВЛЕНО"))
        ).jsonObject.getValue("newItem").jsonObject

    @Test
    fun nullIsNotSent() {
        val newItem = newItemJson(EquipItem(id = "1", location = "Данько Екатерина Михайловна", locationUserId = null))
        assertFalse("locationUserId" in newItem)
    }

    @Test
    fun emptyStringIsSentForManualText() {
        val newItem = newItemJson(EquipItem(id = "1", location = "у Пети в машине", locationUserId = ""))
        assertEquals("", newItem.getValue("locationUserId").jsonPrimitive.content)
    }

    @Test
    fun idIsSent() {
        val newItem = newItemJson(EquipItem(id = "1", location = "склад", locationUserId = WAREHOUSE_ID))
        assertEquals(WAREHOUSE_ID, newItem.getValue("locationUserId").jsonPrimitive.content)
    }

    @Test
    fun responseIsParsed() {
        val response = json.decodeFromString<EquipResponse>(
            """{"success":true,"equipItem":{"id":"2706","location":"Склад","locationUserId":"warehouse"}}"""
        )
        assertEquals(WAREHOUSE_ID, response.equipItem?.toEquipItem()?.locationUserId)
    }
}
