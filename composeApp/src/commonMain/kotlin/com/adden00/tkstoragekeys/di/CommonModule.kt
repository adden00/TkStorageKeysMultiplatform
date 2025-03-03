package com.adden00.tkstoragekeys.di

import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.data.network.StorageApiService
import com.adden00.tkstoragekeys.features.add_equip_screen.NewEquipViewModel
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


private const val BASE_API_URL = "https://script.google.com/macros/s"
private fun dataModule() = module {
    factory<StorageRepository> {
        StorageRepository(
            api = get()
        )
    }



    factory<HttpClient> {
        HttpClient {
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        // Custom log logic, for example, log to a file
                        println("Custom Log: $message")
                    }
                }
            }

//            install(HttpRedirect) {
//                checkHttpMethod = false
//            }
//            followRedirects = false

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    factory<StorageApiService> {
        StorageApiService(
            api = get(),
            baseUrl = BASE_API_URL
        )
    }
}


fun viewModelModule() = module {
    viewModel {
        ReceptionViewModel()
    }

    viewModel {
        NewEquipViewModel()
    }
}

fun getCommonModules() = listOf(dataModule(), viewModelModule())

fun initKoin() {
    startKoin {
        modules(getCommonModules())
    }
}
