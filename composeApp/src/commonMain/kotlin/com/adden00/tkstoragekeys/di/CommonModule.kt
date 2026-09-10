package com.adden00.tkstoragekeys.di

import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.StorageRepository
import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.network.BackendApiService
import com.adden00.tkstoragekeys.data.network.StorageApi
import com.adden00.tkstoragekeys.features.add_equip_screen.NewEquipViewModel
import com.adden00.tkstoragekeys.features.item_history_screen.ItemHistoryViewModel
import com.adden00.tkstoragekeys.features.people_search_screen.PeopleSearchViewModel
import com.adden00.tkstoragekeys.features.person_details_screen.PersonDetailsViewModel
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionViewModel
import com.adden00.tkstoragekeys.features.users_search.UsersSearchViewModel
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private const val SPRING_API_URL = Constants.SPRING_BASE_URL

private fun dataModule() = module {

    factory<HttpClient> {
        HttpClient {
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        // Custom log logic, for example, log to a file
                        println("Custom Log: $message")
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    factory<StorageApi> {
        val appSettings = get<AppSettings>()
        BackendApiService(
            api = get(),
            baseUrlProvider = { if (appSettings.isTestEnv) Constants.TEST_BASE_URL else SPRING_API_URL }
        )
    }

    factory<AppSettings> {
        AppSettings(
            settings = Settings()
        )
    }

    factory<StorageRepository> {
        StorageRepository(
            api = get<StorageApi>(),
            appSettings = get()
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

    viewModel {
        PeopleSearchViewModel()
    }

    viewModel {
        ItemHistoryViewModel()
    }

    viewModel {
        UsersSearchViewModel()
    }

    viewModel {
        PersonDetailsViewModel()
    }
}

fun getCommonModules() = listOf(dataModule(), viewModelModule())

fun initKoin() {
    startKoin {
        modules(getCommonModules())
    }
}
