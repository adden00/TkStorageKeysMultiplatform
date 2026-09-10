package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.model.AddItemRequest
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.EquipsResponse
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.ExportSheetsResponse
import com.adden00.tkstoragekeys.data.model.IdResponse
import com.adden00.tkstoragekeys.data.model.ItemHistoryResponse
import com.adden00.tkstoragekeys.data.model.UpdateItemRequest
import com.adden00.tkstoragekeys.data.model.UserResponse
import com.adden00.tkstoragekeys.data.model.UserSearchResponse
import com.adden00.tkstoragekeys.data.model.UsersImportResponse
import com.adden00.tkstoragekeys.data.model.toDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class BackendApiService(
    api: HttpClient,
    private val baseUrlProvider: () -> String,
) : StorageApi {

    // окружение переключается при логине, поэтому адрес читаем на каждый запрос
    private val base: String get() = baseUrlProvider().trimEnd('/')
    private val api = api.config {
        defaultRequest {
            header("X-App-Version-Code", Constants.VERSION_CODE.toString())
        }
    }

    override suspend fun getItem(id: String): EquipResponse =
        api.get("$base/items/$id").body()

    override suspend fun getAllItems(): EquipsResponse =
        api.get("$base/items").body()

    override suspend fun search(query: String): EquipsResponse =
        api.get("$base/items/search") {
            url { parameters.append("query", query) }
        }.body()

    override suspend fun getFreeId(): IdResponse =
        api.get("$base/items/free-id").body()

    override suspend fun addItem(
        keyholderName: String,
        item: EquipItem,
    ): EquipResponse =
        api.post("$base/items/add") {
            contentType(ContentType.Application.Json)
            setBody(AddItemRequest(newItem = item.toDto(), keyholderName = keyholderName))
        }.body()

    override suspend fun updateItem(
        keyholderName: String,
        id: String,
        item: EquipItem,
        historyAction: String,
    ): EquipResponse =
        api.put("$base/items/update/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateItemRequest(newItem = item.toDto(), keyholderName = keyholderName, historyAction = historyAction))
        }.body()

    override suspend fun getItemHistory(id: String): ItemHistoryResponse =
        api.get("$base/items/$id/history").body()

    override suspend fun exportItems(format: ExportFormat): ByteArray =
        api.get("$base/items/export/${format.path}").body()

    override suspend fun exportToSheets(): ExportSheetsResponse =
        api.post("$base/items/export/sheets").body()

    override suspend fun searchUsers(query: String): UserSearchResponse =
        api.get("$base/users/search") {
            url { parameters.append("query", query) }
        }.body()

    override suspend fun getUser(id: String): UserResponse =
        api.get("$base/users/$id").body()

    override suspend fun getUserItems(id: String): EquipsResponse =
        api.get("$base/users/$id/items").body()

    override suspend fun importUsers(): UsersImportResponse =
        api.post("$base/users/import/sheets") {
            // импорт справочника идёт около минуты
            timeout { requestTimeoutMillis = 120_000 }
        }.body()
}
