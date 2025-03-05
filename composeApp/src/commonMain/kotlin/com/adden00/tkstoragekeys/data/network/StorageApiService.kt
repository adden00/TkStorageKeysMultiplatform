package com.adden00.tkstoragekeys.data.network

import com.adden00.tkstoragekeys.data.model.EquipDto
import com.adden00.tkstoragekeys.data.model.EquipResponse
import com.adden00.tkstoragekeys.data.model.IdResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.contentType

private const val HASH_PROD = "AKfycbzLF7f0QzP48HbrIVTUsMNOqYQi8equCWPBXO0hA4r7DNJ5p01A5XOEn-tHlvlhUq_w"
private const val HASH_TEST = "AKfycbxN3W1mhDwQnMBvle5diYuyThD_W6RpVHT5jJZVpS1sxeTePAoYIpokLcrcZy9DM407"
private const val HASH = HASH_PROD


class StorageApiService(
    private val api: HttpClient,
    private val baseUrl: String
) {

   suspend fun getItem(
       id: String,
       type: String = "get"
    ): EquipResponse {
       val url = URLBuilder("$baseUrl/$HASH/exec").apply {
           parameters.append("id", id)
           parameters.append("type", type)
       }
       val response = api.post(url.buildString())
       return runRedirect(response)
   }


    suspend fun getFreeId(
        type: String = "getFreeId"
    ): IdResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("type", type)
        }
        val response = api.post(url.buildString())
        return runRedirect(response)
    }

    suspend fun updateItem(
        id: String,
        item: EquipDto,
        type: String = "update",
    ): EquipResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("id", id)
            parameters.append("type", type)
        }
        val response = api.post(url.buildString()) {
            headers.append("mode", "no-cors")
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return runRedirect(response)

    }

    suspend fun addItem(
        //todo засунуть всё в параметры
        id: String,
        item: EquipDto,
        type: String = "add",
    ): EquipResponse {
        val url = URLBuilder("$baseUrl/$HASH/exec").apply {
            parameters.append("id", id)
            parameters.append("type", type)
        }
        val response = api.post(url.buildString()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return runRedirect(response)

    }
    private suspend inline fun <reified T> runRedirect(response: HttpResponse): T {
        if (response.status == HttpStatusCode.Found || response.status == HttpStatusCode.MovedPermanently) {
            // Поймали редирект, выполняем GET запрос по новому URL
            val redirectUrl = response.headers[HttpHeaders.Location]
            if (redirectUrl != null) {
                val getResponse = api.get(redirectUrl)
                return getResponse.body()
            } else {
                return response.body()
            }
        } else {
            return response.body()
        }
    }
}
