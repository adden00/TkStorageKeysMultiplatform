package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.EquipsResponse
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ExportSheetsResponse
import com.adden00.tkstoragekeys.data.model.IdResponse
import com.adden00.tkstoragekeys.data.model.ItemHistoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder

private const val HASH_PROD = "AKfycbzLF7f0QzP48HbrIVTUsMNOqYQi8equCWPBXO0hA4r7DNJ5p01A5XOEn-tHlvlhUq_w"
private const val HASH_TEST = "AKfycbxN3W1mhDwQnMBvle5diYuyThD_W6RpVHT5jJZVpS1sxeTePAoYIpokLcrcZy9DM407"
private const val HASH = HASH_PROD

class StorageApiService(
    private val api: HttpClient,
    private val baseUrl: String
) : StorageApi {

    private val versionCode = Constants.VERSION_CODE.toString()

    override suspend fun getItem(id: String): EquipResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("id", id)
            parameters.append("type", "get")
            parameters.append("versionCode", versionCode)
        }
        return runRedirect(api.post(url.buildString()))
    }

    override suspend fun searchByLocation(query: String): EquipsResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("query", query)
            parameters.append("type", "search")
            parameters.append("versionCode", versionCode)
        }
        return runRedirect(api.post(url.buildString()))
    }

    override suspend fun getAllItems(): EquipsResponse {
        TODO("Not supported by Google Apps Script API")
    }

    override suspend fun getFreeId(): IdResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("type", "getFreeId")
            parameters.append("versionCode", versionCode)
        }
        return runRedirect(api.post(url.buildString()))
    }

    override suspend fun updateItem(
        keyholderName: String,
        id: String,
        item: EquipItem,
    ): EquipResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("keyholderName", keyholderName)
            parameters.append("id", id)
            parameters.append("type", "update")
            parameters.append("itemId", item.id)
            parameters.append("category", item.category)
            parameters.append("brand", item.brand)
            parameters.append("name", item.name)
            parameters.append("color", item.color)
            parameters.append("weigh", item.weigh)
            parameters.append("quality", item.quality?.value.orEmpty())
            parameters.append("location", item.location)
            parameters.append("event", item.event)
            parameters.append("date", item.date)
            parameters.append("info", item.info)
            parameters.append("versionCode", versionCode)
        }
        return runRedirect(api.post(url.buildString()))
    }

    override suspend fun addItem(
        keyholderName: String,
        item: EquipItem,
    ): EquipResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("keyholderName", keyholderName)
            parameters.append("id", item.id)
            parameters.append("type", "add")
            parameters.append("itemId", item.id)
            parameters.append("category", item.category)
            parameters.append("brand", item.brand)
            parameters.append("name", item.name)
            parameters.append("color", item.color)
            parameters.append("weigh", item.weigh)
            parameters.append("quality", item.quality?.value.orEmpty())
            parameters.append("location", item.location)
            parameters.append("event", item.event)
            parameters.append("date", item.date)
            parameters.append("info", item.info)
            parameters.append("versionCode", versionCode)
        }
        return runRedirect(api.post(url.buildString()))
    }

    override suspend fun searchByName(query: String): EquipsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getItemHistory(id: String): ItemHistoryResponse {
        TODO("Not supported by Google Apps Script API")
    }

    override suspend fun exportItems(format: ExportFormat): ByteArray {
        TODO("Not supported by Google Apps Script API")
    }

    override suspend fun exportToSheets(): ExportSheetsResponse {
        TODO("Not supported by Google Apps Script API")
    }

    private suspend inline fun <reified T> runRedirect(response: HttpResponse): T {
        if (response.status == HttpStatusCode.Found || response.status == HttpStatusCode.MovedPermanently) {
            val redirectUrl = response.headers[HttpHeaders.Location]
            if (redirectUrl != null) {
                return api.get(redirectUrl).body()
            }
        }
        return response.body()
    }
}
